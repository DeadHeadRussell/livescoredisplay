package edu.cmu.mat.parsers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.cmu.mat.parsers.exceptions.CompilerException;
import edu.cmu.mat.parsers.exceptions.UnexpectedNodeException;
import edu.cmu.mat.scores.Image;
import edu.cmu.mat.scores.Score;

public class XmlParser implements Parser {
	private enum EVENT_TYPES {
		B(0), BAR(1), BEAT_OFFSET(2), TIME_SIGNATURE(3), SECTION(4);

		private static final int EVENT_START = 10000;
		private int _offset;

		private EVENT_TYPES(int offset) {
			_offset = offset;
		}

		public int toValue() {
			return EVENT_START + _offset;
		}
	}

	private static DocumentBuilderFactory XML_FACTORY = DocumentBuilderFactory
			.newInstance();
	{
		XML_FACTORY.setValidating(true);
		XML_FACTORY.setIgnoringElementContentWhitespace(true);
	}

	public Score parse(String scoreName, File score, List<Image> images)
			throws CompilerException, FileNotFoundException, IOException {
		Document xml = null;
		try {
			xml = readXmlFile(score);
		} catch (ParserConfigurationException e) {
			// TODO: Throw compiler parse error.
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Node root = xml.getFirstChild();
		NodeList pages = root.getChildNodes();
		for (int i = 0; i < pages.getLength(); i++) {
			Node page = pages.item(i);

			checkName(page, Arrays.asList("page"));
			// Handle page

			NodeList systems = page.getChildNodes();
			for (int j = 0; j < systems.getLength(); j++) {
				Node system = systems.item(j);

				checkName(system, Arrays.asList("system"));
				// Handle system

				NodeList blocks = system.getChildNodes();
				for (int k = 0; k < blocks.getLength(); k++) {
					Node block = systems.item(k);

					String name = checkName(block,
							Arrays.asList("notes", "block"));
					if (name.equals("notes")) {
						// Notes are currently unused.
						continue;
					}

					NodeList events = block.getChildNodes();
					for (int l = 0; l < events.getLength(); l++) {
						Node event = events.item(l);

						checkName(event, Arrays.asList("event"));

						NamedNodeMap attrs = event.getAttributes();
						int event_code = Integer.parseInt(attrs.getNamedItem(
								"cmd").getNodeValue());

						switch (event_code) {
						default:
							break;
						}
					}
				}
			}
		}

		return new Score(scoreName, null, null);
	}

	private Document readXmlFile(File file)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilder xml_builder = XML_FACTORY.newDocumentBuilder();
		Document xml = xml_builder.parse(file);
		xml.normalize();
		return xml;
	}

	private String checkName(Node node, List<String> expected_names)
			throws UnexpectedNodeException {
		String name = node.getLocalName().toLowerCase();
		if (!expected_names.contains(name)) {
			throw new UnexpectedNodeException("Expected " + expected_names
					+ " node. Found " + name + "instead");
		}
		return name;
	}

	@Override
	public Score parse(String name, File score, List<Image> images, int currentH)
			throws CompilerException, FileNotFoundException, IOException {
		// TODO Auto-generated method stub
		return null;
	}
}
