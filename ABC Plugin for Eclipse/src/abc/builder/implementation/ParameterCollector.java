package abc.builder.implementation;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IAnnotationBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;

public class ParameterCollector extends ASTVisitor
{
	/**
	 * first key is method name. Then the value is a mapping of the methods parameters
	 * to their annotations. param names are stored as simple names, methods names are
	 * stored as keys.
	 */
	HashMap<String, HashMap<String, ArrayList<String>>> map = new HashMap<>();
	@Override
	public boolean visit(MethodDeclaration methodDeclNode)
	{
		IMethodBinding methodBinding = methodDeclNode.resolveBinding();
		ITypeBinding[] paramBindings = methodBinding.getParameterTypes();
		if(paramBindings != null && paramBindings.length > 0)
		{
			HashMap<String, ArrayList<String>> paramToAnnotations = new HashMap<>();
			for(ITypeBinding param : paramBindings)
			{
				
				ArrayList<String> annotationNames = new ArrayList<>();
				
				IAnnotationBinding[] annotationBindings = param.getAnnotations();
				for(IAnnotationBinding a : annotationBindings)
				{
					annotationNames.add(a.getName());
				}
				paramToAnnotations.put(param.getName(), annotationNames);
			}
			
			
			map.put(methodBinding.getKey(), paramToAnnotations);
		}
		
		return true;
	}
}
