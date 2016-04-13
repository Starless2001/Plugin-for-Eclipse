package abc.builder.implementation;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IAnnotationBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;

public class MethodCollector extends ASTVisitor
{
	public HashMap<String, ArrayList<String>> methodsToAnnotations = new HashMap<>();
	
	@Override
	public boolean visit(MethodDeclaration methodDeclNode)
	{
		ArrayList<String> annotationNames = new ArrayList<>();
		
		IMethodBinding methodBinding = methodDeclNode.resolveBinding();
		IAnnotationBinding[] annotationBindings = methodBinding.getAnnotations();
		if(annotationBindings != null && annotationBindings.length > 0)
		{
			for(IAnnotationBinding a : annotationBindings)
			{
				annotationNames.add(a.getName());
			}
			
			methodsToAnnotations.put(methodBinding.getKey(), annotationNames);
		}
		
		return true;
	}
}
