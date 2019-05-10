package PetSitters.serviceLocator;

import PetSitters.exception.ExceptionServiceError;
import PetSitters.factories.Factory;

public class ServiceLocator {
    private static ServiceLocator instance;

    private ServiceLocator() {
        //private constructor
    }

    public static ServiceLocator getInstance() {
        return BillPughSingleton.instance;
    }

    public Service find(String service) throws ExceptionServiceError {
        if (service.equals("GetCoordinates")) {
            Factory f = Factory.getInstance();
            return f.getCityConversor();
        }
        throw new ExceptionServiceError("The service does not exist");
    }

    private static class BillPughSingleton {
        private static final ServiceLocator instance = new ServiceLocator();
    }
}
