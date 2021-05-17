package de.bublitz.balancer.server.service.impl;

import de.bublitz.balancer.server.model.Error;
import de.bublitz.balancer.server.repository.ErrorRepository;
import de.bublitz.balancer.server.service.ErrorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class ErrorServiceImpl implements ErrorService {

    @Autowired
    private ErrorRepository errorRepository;

    @Override
    public void addError(Exception ex) {
        Error tmpError = new Error(ex);
        errorRepository.save(tmpError);
    }

    @Override
    public void deleteAll() {
        errorRepository.deleteAll();
    }

    @Override
    public List<Error> getAllErrors() {
        return errorRepository.findAll();
    }
}
