package abc.builder.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.FIELD, ElementType.LOCAL_VARIABLE})
@Inherited
/** 
 * When applied to a field, ensures the field cannot be aliased (unless to a local @Const)
 * and that no code can alter the state of that field from outside classes that owns it.
 * Any method inside the class can alter the state of the field freely, but, for outside
 * code, the field is treated as {@code @Const}, meaning, from outside, it can't be
 * redefined, used as a parameter for a method that doesn't declare the corresponding
 * formal parameter as {@code @Const}, or have any of its instance methods called, unless
 * they are {@code @Const}
 * 
 * @author Starless2001
 *
 */
public @interface ReadOut
{

}