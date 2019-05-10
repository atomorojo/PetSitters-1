package PetSitters.factories;

import PetSitters.cityConversor.CityConversorProxy;
import PetSitters.cityConversor.ICityConversor;

public class Factory {
    private static Factory instance;
    ICityConversor cityConversor;

    private Factory() {
        //private constructor
    }

    public static Factory getInstance() {
        return BillPughSingleton.instance;
    }

    public ICityConversor getCityConversor() {
        if (cityConversor == null) {
            cityConversor = new CityConversorProxy();
        }
        return cityConversor;
    }

    private static class BillPughSingleton {
        private static final Factory instance = new Factory();
    }

}
