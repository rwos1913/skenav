package skenav.core;

import io.dropwizard.cli.Command;
import io.dropwizard.setup.Bootstrap;
import net.sourceforge.argparse4j.inf.Subparser;

import javax.xml.stream.events.Namespace;

public class Commands extends Command {
	public Commands() {
		super("clisetup", "Starts setup using the CLI");
	}

	@Override
	public void configure(Subparser subparser) {

	}

	@Override
	public void run (Bootstrap<?> bootstrap, Namespace namespace) throws Exception {
	}
}
