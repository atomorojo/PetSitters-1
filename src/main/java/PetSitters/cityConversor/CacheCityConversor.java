package PetSitters.cityConversor;

import PetSitters.auxiliary.CustomLinkedList;
import PetSitters.auxiliary.LRUCache;
import PetSitters.auxiliary.Node;
import PetSitters.exception.ExceptionCache;
import PetSitters.exception.ExceptionServiceError;
import PetSitters.serviceDTO.DTO;

import java.util.HashMap;

// LRU Algorithm. Insertion time: O(1), replacement time: O(1), retrieval time: O(1)

public class CacheCityConversor implements ICityConversor {

    LRUCache<DTO,DTO> LRUCache;

    public CacheCityConversor(int sizeLimit) {
        LRUCache = new LRUCache<>(sizeLimit);
    }

    public int getSizeLimit() {
        return LRUCache.getSizeLimit();
    }

    public void setSizeLimit(int sizeLimit) {
        LRUCache.setSizeLimit(sizeLimit);
    }

    @Override
    public DTO execute(DTO parameter) throws ExceptionServiceError {
        try {
            return LRUCache.getFromCache(parameter);
        } catch (ExceptionCache exceptionCache) {
            throw new ExceptionServiceError("There is no cached result for this parameter");
        }
    }

    public void update(DTO parameter, DTO result) {
        LRUCache.update(parameter, result);
    }
}
