package PetSitters.cityConversor;

import PetSitters.auxiliary.CustomLinkedList;
import PetSitters.auxiliary.Node;
import PetSitters.exception.ExceptionServiceError;
import PetSitters.serviceDTO.DTO;

import java.util.*;

// LRU Algorithm. Insertion time: O(1), replacement time: O(1), retrieval time: O(1)

public class CacheCityConversor implements ICityConversor {

    private int sizeLimit;

    private int counterId;

    HashMap<DTO, MapContent> map;
    CustomLinkedList<DTO> chain;

    public int getSizeLimit() {
        return sizeLimit;
    }

    public void setSizeLimit(int sizeLimit) {
        this.sizeLimit = sizeLimit;
    }

    private class MapContent {
        Node<DTO> pointer;
        DTO coordinates;

        public MapContent(Node<DTO> pointer, DTO coordinates) {
            this.pointer = pointer;
            this.coordinates = coordinates;
        }

        public Node<DTO> getPointer() {
            return pointer;
        }

        public void setPointer(Node<DTO> pointer) {
            this.pointer = pointer;
        }

        public DTO getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(DTO coordinates) {
            this.coordinates = coordinates;
        }
    }

    public CacheCityConversor (int sizeLimit) {
        map = new HashMap<>();
        counterId = 0;
        chain = new CustomLinkedList<DTO>();
        this.sizeLimit = sizeLimit;
    }

    private void deleteLeastRecentlyUsed () {
        Node<DTO> key = chain.getFirstReference();
        map.remove(key.getElem());
        chain.popFirst();
    }

    @Override
    public DTO execute(DTO parameter) throws ExceptionServiceError {
        if (map.containsKey(parameter)) {
            MapContent mapContent = map.get(parameter);
            chain.removeByReference(mapContent.pointer);
            chain.addLast(parameter);
            mapContent.setPointer(chain.getLastReference());
            map.put(parameter, mapContent);
            return mapContent.getCoordinates();
        } else {
            throw new ExceptionServiceError("There is no cached result for this parameter");
        }
    }

    public void update(DTO parameter, DTO result) {
        if (map.containsKey(parameter))  {
            MapContent mapContent = map.get(parameter);
            chain.removeByReference(mapContent.pointer);
        }

        if (map.size() >= sizeLimit) {
            deleteLeastRecentlyUsed();
        }

        chain.addLast(parameter);
        Node<DTO> ite = chain.getLastReference();
        map.put(parameter, new MapContent(ite, result));
    }

}
