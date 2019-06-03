package PetSitters.translation;

import PetSitters.serviceDTO.DTO;
import PetSitters.serviceDTO.DTOTranslationIncoming;
import PetSitters.serviceDTO.DTOTranslationOutgoing;

import java.util.Iterator;
import java.util.LinkedList;

public class TranslationProxy implements ITranslation {
    private CacheTranslation cacheTranslation;
    private GoogleTranslateAPI googleTranslateAPI;

    public TranslationProxy() {
        cacheTranslation = new CacheTranslation(5000000);
        googleTranslateAPI = new GoogleTranslateAPI();
    }

    @Override
    public DTO execute(DTO parameter) throws Exception {
        // DTO are reduced to an element only
        DTOTranslationIncoming dtoParameter = (DTOTranslationIncoming)parameter;
        DTOTranslationOutgoing result = (DTOTranslationOutgoing)cacheTranslation.execute(dtoParameter);
        if (!result.isFullyResolved()) {
            LinkedList<String> inputList = dtoParameter.getText();
            LinkedList<String> outputList = result.getText();
            Iterator<String> iteratorInput = inputList.iterator();
            Iterator<String> iteratorOutput = outputList.iterator();
            LinkedList<String> queryRewrite = new LinkedList<>();
            while (iteratorInput.hasNext() && iteratorOutput.hasNext()) {
                String tmp = iteratorOutput.next();
                String inp = iteratorInput.next();
                if (tmp == null) {
                    queryRewrite.addLast(inp);
                }
            }
            DTOTranslationIncoming queryRewriteParam = new DTOTranslationIncoming(queryRewrite, dtoParameter.getTargetLanguage());
            DTOTranslationOutgoing resultService = (DTOTranslationOutgoing)googleTranslateAPI.execute(queryRewriteParam);
            cacheTranslation.update(queryRewriteParam, resultService);
            result.merge(resultService);
        }
        return result;
    }
}