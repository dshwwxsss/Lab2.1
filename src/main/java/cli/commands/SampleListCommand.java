package cli.commands;

import cli.Command;
import cli.Environment;
import validation.ValidationException;
import domain.Sample;
import java.util.List;

public class SampleListCommand extends Command {
    public SampleListCommand(Environment env) {
        super(env);
    }

    @Override
    public void execute(List<String> args) throws ValidationException {
        var samples = env.getSampleService().getSamples();
        System.out.println("ID\tНазвание");
        for (Sample s : samples) {
            System.out.println(s.getId() + "\t" + s.getName());
        }
    }

    @Override
    public String getHelp() {
        return "список всех образцов";
    }
}