package org.kcl.nestor.agent.action;

import java.io.File;
import java.io.FileOutputStream;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import jason.JasonException;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.StringTerm;
import jason.asSyntax.Term;

/**
 * An internal action that allows an agent to save its program description.
 * @author meneguzz
 *
 */
public class export_agent_program extends DefaultInternalAction {

	public Object execute(TransitionSystem ts, Unifier un, Term[] args)
			throws Exception {
		
		if(args.length != 1) {
			throw new JasonException("The internal action 'export_agent_program' has not received the required argument.");
		}
		
		if(args[0].isString()) {
			StringTerm target = (StringTerm) args[0];
			File file = new File(target.getString());
			if(!file.exists() || file.canWrite()) {
				FileOutputStream outputStream = new FileOutputStream(file);
				
				Document doc = ts.getAg().getAgProgram();
				DOMSource source = new DOMSource(doc);
				
				Transformer transformer = TransformerFactory.newInstance().newTransformer();
				StreamResult result = new StreamResult(outputStream);
				transformer.setParameter(OutputKeys.INDENT, "yes");
				
				ts.getAg().getLogger().info("Writing agent program to '"+file.toString()+"'.");
				
				transformer.transform(source, result);
			} else {
				throw new JasonException("File '"+file.toString()+"' cannot be overwritten.");
			}
		} else {
			throw new JasonException("The internal action 'export_agent_program' requires a string argument.");
		}
		
		return true;
	}
}
