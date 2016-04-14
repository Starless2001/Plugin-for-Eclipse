package abc.builder.implementation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IAnnotationBinding;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.VariableDeclaration;

public class FieldCollector extends ASTVisitor
{
	/**
	 * name of the field is a key, the value is a string of annotations.
	 */ 	
	 public HashMap<String, ArrayList<String>> fieldsToAnnotations = new HashMap<>();
	 
	
	@Override
	public boolean visit(FieldDeclaration fieldDeclNode)
	{
		//fieldDeclNode.setModifiers(fieldDeclNode.getModifiers() | Modifier.FINAL);
		//gets names declared (probably one):
		
		@SuppressWarnings("unchecked")
		List<IExtendedModifier> modifiers = (List<IExtendedModifier>) fieldDeclNode.getStructuralProperty(FieldDeclaration.MODIFIERS2_PROPERTY);
		ArrayList<String> annotationNames = new ArrayList<>();
		
		for(IExtendedModifier modifier : modifiers)
		{
			if(modifier instanceof Annotation)
			{
				Annotation annotation = (Annotation) modifier;
				IAnnotationBinding binding = annotation.resolveAnnotationBinding();
				if(binding != null)
				{
					annotationNames.add(binding.getAnnotationType().getQualifiedName());
				}
			}
		}
		
		@SuppressWarnings("unchecked")
		List<VariableDeclaration> fieldsInOneDeclaration = fieldDeclNode.fragments();
		
		for(VariableDeclaration field : fieldsInOneDeclaration)
		{
			IVariableBinding binding = field.resolveBinding();
			if(binding != null)
			{
				fieldsToAnnotations.put(binding.getName(), annotationNames);
			}
		}
		
		//disallow aliasing of fields. right side of assignment
		//disallow redefinition. left side of assignment
		//validate passing a field as argument: corresponding formal parameter must be @Const
		//validate instance method invocations on fields: instance method must be @Const
		return true;
	}
}
