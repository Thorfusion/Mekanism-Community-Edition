package mekanism.api.gas;

import net.minecraftforge.fluids.Fluid;

import java.util.ArrayList;
import java.util.List;

public class GasRegistry {
    private static ArrayList<Gas> registeredGasses = new ArrayList<Gas>();

    /**
     * Register a new gas into GasRegistry.
     * @param gas - Gas to register
     * @return the gas that has been registered, pulled right out of GasRegistry
     */
    public static Gas register(Gas gas) {
        if (gas == null) {
            return null;
        }
        registeredGasses.add(gas);
        return getGas(gas.getName());
    }

    /**
     * Gets the gas associated with the defined ID.
     * @param id - ID to check
     * @return gas associated with defined ID
     */
    public static Gas getGas(int id) {
        return id == -1 ? null : registeredGasses.get(id);
    }

    /**
     * Gets the gas associated with the defined fluid.
     * @param f - fluid to check
     * @return the gas associated with the fluid
     */
    public static Gas getGas(Fluid f) {
        return registeredGasses.stream()
                .filter(gas -> gas.hasFluid() && gas.getFluid() == f)
                .findAny()
                .orElse(null);
    }

    /**
     * Whether or not GasRegistry contains a gas with the specified name
     * @param name - name to check
     * @return if GasRegistry contains a gas with the defined name
     */
    public static boolean containsGas(String name) {
        return getGas(name) != null;
    }

    /**
     * Gets the list of all gasses registered in GasRegistry.
     * @return a cloned list of all registered gasses
     */
    public static List<Gas> getRegisteredGasses() {
        return (List<Gas>)registeredGasses.clone();
    }

    /**
     * Gets the gas associated with the specified name.
     * @param name - name of the gas to get
     * @return gas associated with the name
     */
    public static Gas getGas(String name) {
        return registeredGasses.stream()
                .filter(gas -> gas.getName().equalsIgnoreCase(name))
                .findAny()
                .orElse(null);
    }

    /**
     * Gets the gas ID of a specified gas.
     * @param gas - gas to get the ID from
     * @return gas ID
     */
    public static int getGasID(Gas gas) {
        return gas == null || !containsGas(gas.getName()) ? -1 : registeredGasses.indexOf(gas);
    }
}