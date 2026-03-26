package cli; //шаблон для всех команд

import validation.ValidationException;

import java.util.List;

public abstract class Command {
    protected Environment env;

    public Command(Environment env) { //при создании команды мы передаём ей окружение
        this.env = env;
    }

    public abstract void execute(List<String> args) throws ValidationException;

    public abstract String getHelp(); //команда должна вернуть описание того, что она делает

    public void checkArgs(List<String> args) throws ValidationException {
        //по умолчанию ничего не делает
        //команды, которым нужна проверка аргументов, могут переопределить этот метод
        //если проверка не пройдена, метод выбрасывает ValidationException
    }

    public boolean isReqAdditionalInput() { //возвращает true,
        // если команде требуется дополнительный интерактивный ввод, по умолчанию false
        return false;
    }

    public void performAdditionalInput(List<String> args) throws ValidationException {

    }
}
