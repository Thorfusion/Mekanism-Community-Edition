#!/usr/bin/env bash

set -euo pipefail
shopt -s nullglob

artifact_dir="${1:-output}"

if [[ ! -d "$artifact_dir" ]]; then
    echo "Artifact directory does not exist: $artifact_dir" >&2
    exit 1
fi

declare -a expected=(
    "ALL:jar"
    "API:jar"
    "Core:jar"
    "DEV:jar"
    "DefenseTech:jar"
    "Generators:jar"
    "MDK:zip"
    "Tools:jar"
    "Ultimate:jar"
)

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

for spec in "${expected[@]}"; do
    classifier="${spec%%:*}"
    extension="${spec##*:}"
    matches=("$artifact_dir"/Mekanism-Community-Edition-*-"$classifier"."$extension")

    if (( ${#matches[@]} != 1 )); then
        echo "Expected exactly one $classifier artifact, found ${#matches[@]}." >&2
        printf '  %s\n' "${matches[@]:-<none>}" >&2
        exit 1
    fi

    artifact="${matches[0]}"
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
        ALL|Core)
            require_exact_entry 'mekanism/common/Mekanism.class' "$artifact"
            ;;
        API)
            require_matching_entry '^mekanism/api/.*\.class$' 'compiled Mekanism API class' "$artifact"
            ;;
        DEV)
            require_exact_entry 'mekanism/common/Mekanism.class' "$artifact"
            require_exact_entry 'mekanism/common/Mekanism.java' "$artifact"
            ;;
        DefenseTech)
            require_exact_entry 'defense/common/DefenseTech.class' "$artifact"
            ;;
        Generators)
            require_exact_entry 'mekanism/generators/common/MekanismGenerators.class' "$artifact"
            ;;
        MDK)
            require_matching_entry '^mekanism/api/.*\.java$' 'Mekanism API source' "$artifact"
            ;;
        Tools)
            require_exact_entry 'mekanism/tools/common/MekanismTools.class' "$artifact"
            ;;
        Ultimate)
            require_exact_entry 'mcmod.info' "$artifact"
            if ! grep -q '\.class$' "$listing"; then
                echo "::warning file=$artifact::Ultimate currently contains metadata only and will be published as beta."
            fi
            ;;
    esac

    echo "Validated $artifact"
done
