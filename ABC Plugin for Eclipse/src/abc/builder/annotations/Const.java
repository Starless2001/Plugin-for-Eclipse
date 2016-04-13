package abc.builder.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD, ElementType.LOCAL_VARIABLE})
@Inherited
/**
 * When Applied to a method, ensures the method doesn't change in any way the state
 * of the object used to invoke it, i.e., all the fields of the object must remain the same,
 * and no reference field may be returned directly.
 * 
 * When applied to a formal parameter, ensures the method will not modify the value referenced
 * by the formal parameter. A formal parameter annotated as {@code @Const} will not be aliased
 * inside the body of the method, neither the method is allowed to invoke another method and pass
 * the annotated parameter, save if the other method also annotates the parameter as {@code @Const}
 * 
 * When applied to a field, ensures the field cannot be aliased and that no code can alter the
 * state of that field, either from inside the class that declares the field or outside it. Any
 * constructor in any derived class is allowed to set the value of the field, but the field
 * still cannot be aliased. Only methods annotated as {@code @Const} may be invoked
 * using this variable.
 * 
 * When applied to a local variable, ensures neither the block where the variable is declared
 * or any nested block will alter the value of the local variable. The local variable may be defined
 * only once, at any point where it is in scope. Only methods annotated as {@code @Const} may be invoked
 * using this variable.
 * @author Starless2001
 *
 */
public @interface Const
{

}