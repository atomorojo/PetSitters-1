package PetSitters.factories;

import PetSitters.cityConversor.CityConversorProxy;
import PetSitters.cityConversor.ICityConversor;

public class Factory {
    private static Factory instance;

    private Factory() {
        //private constructor
    }

    private static class BillPughSingleton {
        private static final Factory instance = new Factory();
    }

    public static Factory getInstance() {
        return BillPughSingleton.instance;
    }

    ICityConversor cityConversor;

    public ICityConversor getCityConversor() {
        if (cityConversor == null) {
            cityConversor = new CityConversorProxy();
        }
        return cityConversor;
    }

}
