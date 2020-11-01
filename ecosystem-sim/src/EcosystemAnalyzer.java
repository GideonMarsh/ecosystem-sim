import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class EcosystemAnalyzer {
	private PrintWriter out;
	private boolean toConsole;
	
	public EcosystemAnalyzer() {
		out = null;
		toConsole = true;
	}
	
	public EcosystemAnalyzer(String outputFileName) {
		try {
			out = new PrintWriter(new BufferedWriter(new FileWriter(outputFileName)));
			toConsole = false;
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void analyzeEcosystem(OrganismList ecosystem) {
		
	}
}
