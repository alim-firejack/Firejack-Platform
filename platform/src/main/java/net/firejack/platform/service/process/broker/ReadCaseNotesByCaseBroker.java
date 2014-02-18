package net.firejack.platform.service.process.broker;

import net.firejack.platform.api.process.domain.CaseNote;
import net.firejack.platform.core.broker.ListBroker;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.process.CaseNoteModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.store.process.ICaseNoteStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Class encapsulates the functionality of retrieving case notes by case
 */
@TrackDetails
@Component("readCaseNotesByCaseBroker")
public class ReadCaseNotesByCaseBroker extends ListBroker<CaseNoteModel, CaseNote, SimpleIdentifier<Long>> {

    @Autowired
    private ICaseNoteStore store;

    /**
     * Invokes data access layer in order to search for the case notes by the case
     * @param simpleIdentifierServiceRequest service request containing ID of the case
     * @return list of found case notes
     * @throws BusinessFunctionException
     */
    @Override
    protected List<CaseNoteModel> getModelList(ServiceRequest<SimpleIdentifier<Long>> simpleIdentifierServiceRequest) throws BusinessFunctionException {
        return store.findByCaseIdAndSearchTerm(simpleIdentifierServiceRequest.getData().getIdentifier(), null);
    }

}
