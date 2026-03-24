package cli;

import validation.ValidationException;

import java.util.List;

public abstract class Command {
    protected Environment env;

    public Command(Environment env) {
        this.env = env;
    }

    public abstract void execute(List<String> args) throws ValidationException;

    public abstract String getHelp();

    public void checkArgs(List<String> args) throws ValidationException {

    }

    public boolean isReqAdditionalInput() {
        return false;
    }

    public void performAdditionalInput(List<String> args) throws ValidationException {

    }
}
