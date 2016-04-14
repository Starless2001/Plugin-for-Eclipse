package abc.builder.implementation;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class JavaASTParser
{
	public static FieldCollector fieldCollector;
	public static MethodCollector methodCollector;
	public static ParameterCollector parameterCollector;
	
	public static HashMap<IFile, String> messages = new HashMap<>();
	public static void parseProject(IProject project)
	{
		try
		{
			project.open(null /* IProgressMonitor */);
			IJavaProject javaProject = JavaCore.create(project);
			IPackageFragmentRoot[] allPackageRoots = javaProject.getAllPackageFragmentRoots();
			
			if(allPackageRoots == null)
				throw new NullPointerException("allPackageRoots was null");
			if(allPackageRoots.length <= 0)
				throw new IndexOutOfBoundsException("allPackageRoots was empty");
			
			for(int i = 0; i < allPackageRoots.length; ++i)
			{
				if(allPackageRoots[i].getKind() == IPackageFragmentRoot.K_SOURCE)
				{
					IPackageFragmentRoot currentPackageRoot = allPackageRoots[i];
					IJavaElement[] children = currentPackageRoot.getChildren();
					
					if(children != null && children.length > 0)
					{
						for(int j = 0; j < children.length; ++j)
						{
							IJavaElement child = children[i];
							if(child.getElementType() == IJavaElement.COMPILATION_UNIT)
							{
								parseCompilationUnit((ICompilationUnit)child);
							}
						}
					}
				}
			}
		}
		catch (CoreException e)
		{
			System.err.println("Parser.parseProject threw a CoreException\n" + e.getMessage() + "\n");
			e.printStackTrace();
		}
	}
	
	private static void parseCompilationUnit(ICompilationUnit unit) throws JavaModelException
	{
		IFile file = (IFile) unit.getUnderlyingResource();
		
		String text = unit.getElementName() + "\n";
		
		messages.put(file, text);
		
		System.out.println("parsing " + unit.getElementName());
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setResolveBindings(true);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit);
		
		CompilationUnit ast = (CompilationUnit) parser.createAST(null);
		
		fieldCollector = new FieldCollector();
		ast.accept(fieldCollector);
		final Wrapper<String> wrp = new Wrapper<>();
		wrp.element = text;
		fieldCollector.fieldsToAnnotations.forEach((fieldName, annotations) ->
		{
			wrp.element += "F: " + fieldName + " { ";
			for(String annotation : annotations)
			{
				wrp.element += annotation + " ";
			}
			
			wrp.element += "}\n";
		});
		
		text = wrp.element;
		
		methodCollector = new MethodCollector();
		ast.accept(methodCollector);
		
		parameterCollector = new ParameterCollector();
		ast.accept(parameterCollector);
		
		/*ConstEnforcer constEnforcer = new ConstEnforcer();
		ast.accept(constEnforcer);*/
	}
}
