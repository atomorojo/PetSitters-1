package PetSitters.translation;

import PetSitters.auxiliary.LRUCache;
import PetSitters.cityConversor.ICityConversor;
import PetSitters.exception.ExceptionCache;
import PetSitters.exception.ExceptionServiceError;
import PetSitters.serviceDTO.DTO;
import PetSitters.serviceDTO.DTOTranslationIncoming;
import PetSitters.serviceDTO.DTOTranslationOutgoing;

import java.util.Iterator;
import java.util.LinkedList;

public class CacheTranslation implements ICityConversor {

    PetSitters.auxiliary.LRUCache<DTO,DTO> LRUCache;

    public CacheTranslation(int sizeLimit) {
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
        DTOTranslationIncoming dtoTranslationIncoming = (DTOTranslationIncoming)parameter;
        LinkedList<DTOTranslationIncoming> splits = dtoTranslationIncoming.split();
        LinkedList<DTOTranslationOutgoing> output = new LinkedList<>();
        for (DTOTranslationIncoming element: splits) {
            DTOTranslationOutgoing res;
            try {
                res = (DTOTranslationOutgoing)LRUCache.getFromCache(element);
            } catch (ExceptionCache exceptionCache) {
                LinkedList<String> tmp = new LinkedList<>();
                tmp.addLast(null);
                res = new DTOTranslationOutgoing(tmp, false);
            }
            output.addLast(res);
        }
        DTOTranslationOutgoing dtoTranslationOutgoing = new DTOTranslationOutgoing();
        dtoTranslationOutgoing.join(output);
        return dtoTranslationOutgoing;
    }

    public void update(DTO parameter, DTO result) {
        DTOTranslationIncoming dtoTranslationIncoming = (DTOTranslationIncoming)parameter;
        LinkedList<DTOTranslationIncoming> splits = dtoTranslationIncoming.split();
        DTOTranslationOutgoing dtoTranslationOutgoing = (DTOTranslationOutgoing)result;
        LinkedList<DTOTranslationOutgoing> outputSplits = dtoTranslationOutgoing.split();
        Iterator<DTOTranslationIncoming> dtoTranslationIncomingIterator = splits.iterator();
        Iterator<DTOTranslationOutgoing> dtoTranslationOutgoingIterator = outputSplits.iterator();
        while (dtoTranslationIncomingIterator.hasNext() && dtoTranslationOutgoingIterator.hasNext()) {
            LRUCache.update(dtoTranslationIncomingIterator.next(), dtoTranslationOutgoingIterator.next());
        }
    }
}
