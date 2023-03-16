package main.java.rosenhristov.interpreter;

import java.util.LinkedList;
import java.util.List;

import static java.util.Objects.isNull;

public class Errors {
    List<String> errors;

    public Errors() {
    }

    public List<String> getErrors() {
        if (isEmpty(errors)) {
            errors = new LinkedList<>();
        }
        return errors;
    }

    private boolean isEmpty(List<String> list) {
        return isNull(list) || list.isEmpty();
    }

    public boolean addError(String error) {
        return getErrors().add(error);
    }

    public boolean addAll(List<String> errors) {
        return getErrors().addAll(errors);
    }

    public boolean exist() {
        return !isNull(errors) && !errors.isEmpty();
    }

}
