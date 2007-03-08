package rover;

import jason.asSyntax.Literal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class ScriptGenerator {
	
	protected Random random;
	protected int gridSize;
	protected int steps;
	protected int timeBetweenSteps;
	protected OutputStream outputStream;
	protected int firstStep;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ScriptGenerator generator = new ScriptGenerator(args);
		Document script = generator.createScript();
		try {
			generator.writeDocument(script);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ScriptGenerator(String args[]) {
		this.random = new Random();
		this.gridSize = 30;
		this.outputStream = System.out;
		this.steps = 10;
		this.timeBetweenSteps = 1;
		parseArgs(args);
	}
	
	protected void parseArgs(String args[]) {
		for (int i = 0; i < args.length; i++) {
			if(args[i].equals("-o")) {
				if(++i < args.length) {
					File file = new File(args[i]);
					try {
						this.outputStream = new FileOutputStream(file);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					System.err.println("-o parameter requires a filename.");
				}
			} else if(args[i].equals("-gridsize")) {
				if(++i < args.length) {
					try {
						int size = Integer.parseInt(args[i]);
						this.gridSize = size;
					}catch (NumberFormatException e) {
						System.err.println("-gridsize parameter requires an integer");
					}
				} else {
					System.err.println("-gridsize parameter requires an integer");
				}
			} else if(args[i].equals("-steps")) {
				if(++i < args.length) {
					try {
						int steps = Integer.parseInt(args[i]);
						this.steps = steps;
					}catch (NumberFormatException e) {
						System.err.println("-steps parameter requires an integer");
					}
				} else {
					System.err.println("-steps parameter requires an integer");
				}
			} else if (args[i].equals("-stepsize")) {
				if(++i < args.length) {
					try {
						int stepsize = Integer.parseInt(args[i]);
						this.timeBetweenSteps = stepsize;
					}catch (NumberFormatException e) {
						System.err.println("-stepsize parameter requires an integer");
					}
				} else {
					System.err.println("-stepsize parameter requires an integer");
				}
			} else if (args[i].equals("-firststep")) {
				if(++i < args.length) {
					try {
						int firstStep = Integer.parseInt(args[i]);
						this.firstStep = firstStep;
					}catch (NumberFormatException e) {
						System.err.println("-stepsize parameter requires an integer");
					}
				} else {
					System.err.println("-stepsize parameter requires an integer");
				}
			} else {
				System.err.println("Unrecognized parameter: "+args[i]);
			}
		}
	}
	
	public void writeDocument(Document document) throws Exception {
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		StreamResult result = new StreamResult(outputStream);
		DOMSource source = new DOMSource(document);
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.transform(source, result);
	}
	
	public Document createScript() {
		DocumentBuilder builder;
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return null;
		}
		Document document = builder.newDocument();
		
		Element scriptElement = document.createElement("script");
		document.appendChild(scriptElement);
		
		this.addSteps(document, scriptElement, steps);
		
		return document;
	}
	
	protected void addSteps(Document document, Element scriptElement, int steps) {
		for(int i=0; i < steps; i++) {
			List<Literal> percepts = createPercepts(i);
			Element step = createScriptStep(document, i, percepts);
			scriptElement.appendChild(step);
		}
	}
	
	protected List<Literal> createPercepts(int time) {
		List<Literal> percepts = new ArrayList<Literal>();
		
		int x = random.nextInt(gridSize);
		int y = random.nextInt(gridSize);
		
		percepts.add(Literal.parseLiteral("waypoint("+x+","+y+")"));
		
		return percepts;
	}
	
	protected Element createScriptStep(Document document, int time, List<Literal> percepts) {
		Element step = document.createElement("step");
		step.setAttribute("time", ""+(firstStep + (time*timeBetweenSteps)));
		StringBuffer buffer = new StringBuffer();
		
		for (Literal literal : percepts) {
			buffer.append(literal.toString());
			//buffer.append(System.getProperty("line.separator"));
			buffer.append(".\n");
		}
		Text text = document.createTextNode(buffer.toString());
		
		step.appendChild(text);
		
		return step;
	}

}
