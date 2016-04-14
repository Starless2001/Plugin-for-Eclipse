package abc.builder;

import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import abc.builder.implementation.FieldCollector;
import abc.builder.implementation.JavaASTParser;

public class ABCBuilder extends IncrementalProjectBuilder
{
	/*class ABCDeltaVisitor implements IResourceDeltaVisitor
	{
		
		public boolean visit(IResourceDelta delta) throws CoreException
		{
			IResource resource = delta.getResource();
			switch (delta.getKind())
			{
			/*case IResourceDelta.ADDED:
				// handle added resource
				checkXML(resource);
				break;
			case IResourceDelta.REMOVED:
				// handle removed resource
				break;
			case IResourceDelta.CHANGED:
				// handle changed resource
				checkXML(resource);
				break;
				case IResourceDelta.ADDED:
				case IResourceDelta.REMOVED:
				case IResourceDelta.CHANGED:
					resource.accept(new ABCResourceVisitor());
				break;
			}
			//return true to continue visiting children.
			return true;
		}
	}*/

	//UNUSED
	class ABCResourceVisitor implements IResourceVisitor
	{
		@Override
		public boolean visit(IResource resource)
		{
			
			checkXML(resource);
			//return true to continue visiting children.
			return true;
		}
	}

	//DON'T KNOW HOW TO USE THIS
	class XMLErrorHandler extends DefaultHandler
	{
		private IFile file;

		public XMLErrorHandler(IFile file)
		{
			this.file = file;
		}

		private void addMarker(SAXParseException e, int severity)
		{
			ABCBuilder.this.addMarker(file, e.getMessage(), e.getLineNumber(), severity);
		}

		@Override
		public void error(SAXParseException exception) throws SAXException
		{
			addMarker(exception, IMarker.SEVERITY_ERROR);
		}

		@Override
		public void fatalError(SAXParseException exception) throws SAXException
		{
			addMarker(exception, IMarker.SEVERITY_ERROR);
		}

		@Override
		public void warning(SAXParseException exception) throws SAXException
		{
			addMarker(exception, IMarker.SEVERITY_WARNING);
		}
	}

	public static final String BUILDER_ID = "abc.abc_project_builder";

	private static final String MARKER_TYPE = "abc.xmlProblem";

	private SAXParserFactory parserFactory;

	private void addMarker(IFile file, String message, int lineNumber, int severity)
	{
		try
		{
			IMarker marker = file.createMarker(MARKER_TYPE);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, severity);
			if (lineNumber == -1)
			{
				lineNumber = 1;
			}
			marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
		}
		catch (CoreException e)
		{
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.internal.events.InternalBuilder#build(int,
	 *      java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected IProject[] build(int kind, Map<String, String> args, IProgressMonitor monitor)
			throws CoreException
	{
		if (kind == FULL_BUILD)
		{
			fullBuild(monitor);
		}
		/*else
		{
			IResourceDelta delta = getDelta(getProject());
			if (delta == null)
			{
				fullBuild(monitor);
			}
			else
			{
				incrementalBuild(delta, monitor);
			}
		}*/
		return new IProject[] {getProject()};
	}

	@Override
	protected void clean(IProgressMonitor monitor) throws CoreException
	{
		// delete markers set and files created
		getProject().deleteMarkers(MARKER_TYPE, true, IResource.DEPTH_INFINITE);
	}

	void checkXML(IResource resource)
	{
		if (resource instanceof IFile && resource.getName().endsWith(".xml"))
		{
			IFile file = (IFile) resource;
			deleteMarkers(file);
			XMLErrorHandler reporter = new XMLErrorHandler(file);
			try
			{
				getParser().parse(file.getContents(), reporter);
			}
			catch (Exception e1)
			{
			}
		}
	}

	private void deleteMarkers(IFile file)
	{
		try
		{
			file.deleteMarkers(MARKER_TYPE, false, IResource.DEPTH_ZERO);
		}
		catch (CoreException ce)
		{
		}
	}

	protected void fullBuild(final IProgressMonitor monitor)
			throws CoreException
	{
		JavaASTParser.parseProject(getProject());
		JavaASTParser.messages.forEach((file, msg) -> addMarker(file, msg, -1, IMarker.SEVERITY_WARNING));
		/*fieldCollector.fieldsToAnnotations.forEach((fieldName, annotations) ->
		{
			String message = "F: " + fieldName + "@{ ";
			for(String annotation : annotations)
			{
				message += annotation + ", ";
			}
			if(message.length() > 1)
			{
				message = message.substring(0, message.length() - 2);
			}
			message += " }";
			
			System.out.println(message);
		});*/
	}

	private SAXParser getParser()
			throws ParserConfigurationException, SAXException
	{
		if (parserFactory == null)
		{
			parserFactory = SAXParserFactory.newInstance();
		}
		return parserFactory.newSAXParser();
	}

	/*protected void incrementalBuild(IResourceDelta delta, IProgressMonitor monitor)
			throws CoreException
	{
		// the visitor does the work.
		delta.accept(new ABCDeltaVisitor());
	}*/
}
