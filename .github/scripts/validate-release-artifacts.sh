#!/usr/bin/env bash

set -euo pipefail

artifact_dir="${1:-output}"
artifact_prefix="${2:-}"
repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
minecraft_version="$(sed -n 's/^minecraft_version=//p' "$repo_root/gradle.properties" | tr -d '\r' | tail -n 1)"
mod_version="$(sed -n 's/^mod_version=//p' "$repo_root/gradle.properties" | tr -d '\r' | tail -n 1)"

if [[ ! -d "$artifact_dir" ]]; then
    echo "Artifact directory does not exist: $artifact_dir" >&2
    exit 1
fi

if [[ -z "$minecraft_version" || -z "$mod_version" ]]; then
    echo "Could not determine artifact versions from gradle.properties." >&2
    exit 1
fi

if [[ -z "$artifact_prefix" ]]; then
    artifact_prefix="Mekanism-Community-Edition-${minecraft_version}-${mod_version}"

    if [[ -n "${BUILD_VER:-}" ]]; then
        artifact_prefix="${artifact_prefix}-${BUILD_VER}"
    fi
fi

case "$minecraft_version" in
    1.7.10)
        expected=(ALL:jar API:jar Core:jar DEV:jar DefenseTech:jar Generators:jar MDK:zip Tools:jar Ultimate:jar)
        ;;
    1.12.2)
        expected=(ALL:jar API:jar Core:jar Generators:jar Tools:jar Ultimate:jar)
        ;;
    *)
        echo "Unsupported Minecraft version: $minecraft_version" >&2
        exit 1
        ;;
esac

listing="$(mktemp)"
trap 'rm -f -- "$listing"' EXIT

require_exact_entry() {
    local entry="$1"
    local artifact="$2"
    if ! grep -Fqx "$entry" "$listing"; then
        echo "Required entry '$entry' is missing from $artifact" >&2
        exit 1
    fi
}

require_matching_entry() {
    local pattern="$1"
    local description="$2"
    local artifact="$3"
    if ! grep -Eq "$pattern" "$listing"; then
        echo "Required $description is missing from $artifact" >&2
        exit 1
    fi
}

reject_matching_entry() {
    local pattern="$1"
    local description="$2"
    local artifact="$3"
    if grep -Eq "$pattern" "$listing"; then
        echo "Unexpected $description was packaged in $artifact" >&2
        exit 1
    fi
}

for spec in "${expected[@]}"; do
    classifier="${spec%%:*}"
    extension="${spec##*:}"
    artifact="$artifact_dir/$artifact_prefix-$classifier.$extension"

    if [[ ! -f "$artifact" ]]; then
        echo "Expected artifact is missing: $artifact" >&2
        exit 1
    fi

    if (( $(wc -c < "$artifact") < 512 )); then
        echo "Artifact is unexpectedly small: $artifact" >&2
        exit 1
    fi

    jar tf "$artifact" | tr -d '\r' > "$listing"
    if [[ ! -s "$listing" ]]; then
        echo "Artifact is empty or unreadable: $artifact" >&2
        exit 1
    fi

    case "$classifier" in
        Core)
            require_exact_entry 'mekanism/common/Mekanism.class' "$artifact"
            reject_matching_entry '^(mekanism/(generators|tools|ultimate)/|defense/)' 'module class' "$artifact"

            if [[ "$minecraft_version" == '1.12.2' ]]; then
                reject_matching_entry '^assets/mekanism(generators|tools|ultimate)/' 'module asset' "$artifact"
            fi
            ;;
        ALL)
            require_exact_entry 'mekanism/common/Mekanism.class' "$artifact"
            require_exact_entry 'mekanism/generators/common/MekanismGenerators.class' "$artifact"
            require_exact_entry 'mekanism/tools/common/MekanismTools.class' "$artifact"
            require_exact_entry 'mekanism/ultimate/common/MekanismUltimate.class' "$artifact"

            if [[ "$minecraft_version" == '1.7.10' ]]; then
                require_exact_entry 'defense/common/DefenseTech.class' "$artifact"
            fi
            ;;
        API)
            require_matching_entry '^mekanism/api/.*\.class$' 'compiled Mekanism API class' "$artifact"

            if [[ "$minecraft_version" == '1.12.2' ]]; then
                require_matching_entry '^mekanism/api/.*\.java$' 'Mekanism API source' "$artifact"
            fi
            ;;
        DEV)
            require_exact_entry 'mekanism/common/Mekanism.class' "$artifact"
            require_exact_entry 'mekanism/common/Mekanism.java' "$artifact"
            ;;
        DefenseTech)
            require_exact_entry 'defense/common/DefenseTech.class' "$artifact"
            ;;
        Generators)
            require_exact_entry 'mcmod.info' "$artifact"
            require_exact_entry 'mekanism/generators/common/MekanismGenerators.class' "$artifact"
            ;;
        MDK)
            require_matching_entry '^mekanism/api/.*\.java$' 'Mekanism API source' "$artifact"
            ;;
        Tools)
            require_exact_entry 'mcmod.info' "$artifact"
            require_exact_entry 'mekanism/tools/common/MekanismTools.class' "$artifact"
            ;;
        Ultimate)
            require_exact_entry 'mcmod.info' "$artifact"
            require_exact_entry 'mekanism/ultimate/common/MekanismUltimate.class' "$artifact"
            ;;
    esac

    echo "Validated $artifact"
done
