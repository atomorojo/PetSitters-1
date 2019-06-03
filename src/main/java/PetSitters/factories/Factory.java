package PetSitters.factories;

import PetSitters.cityConversor.CityConversorProxy;
import PetSitters.cityConversor.ICityConversor;
import PetSitters.translation.ITranslation;
import PetSitters.translation.TranslationProxy;

public class Factory {
    private static Factory instance;
    ICityConversor cityConversor;
    ITranslation translation;

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

    public ITranslation getTranslation() {
        if (translation == null) {
            translation = new TranslationProxy();
        }
        return translation;
    }

    private static class BillPughSingleton {
        private static final Factory instance = new Factory();
    }

}
