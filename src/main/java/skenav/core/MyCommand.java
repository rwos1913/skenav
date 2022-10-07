package skenav.core;

import io.dropwizard.cli.Command;
import io.dropwizard.setup.Bootstrap;
import net.sourceforge.argparse4j.inf.Subparser;

import javax.xml.stream.events.Namespace;

public class MyCommand extends Command {
	public MyCommand(){
		super ("clisetup", "triggers setup via CLI");
	}

	@Override
	public void configure (Subparser subparser) {}

	@Override
	public void run(Bootstrap<?> bootstrap, net.sourceforge.argparse4j.inf.Namespace namespace) throws Exception {
		Setup setup = new Setup();
		setup.setupWithCli();
	}
}
