/**
Homework 02
============
Note: For some tasks, test examples are already provided.
Be sure to provide tests for all tasks and check your solution with them.
From now on, the tasks will not explicitly require tests any more,
but I advise you to nevertheless use tests for all programming tasks.
*/

/**
Part 1: Desugaring to Nand (1 subtask)
------
*/

import scala.language.implicitConversions

object Hw02Part1 {
/**
Consider again the language of propositional logic formulae from the previous homework:
*/
sealed abstract class Exp
case class True() extends Exp  // constant true
case class False() extends Exp // constant false
case class And(lhs: Exp, rhs: Exp) extends Exp
case class Or(lhs: Exp, rhs: Exp) extends Exp
case class Not(e: Exp) extends Exp
case class Impl(lhs: Exp, rhs: Exp) extends Exp
case class Nand(lhs: Exp, rhs: Exp) extends Exp
  
def eval(e: Exp) : Boolean = e match {
  case True()                 => true
  case False()                => false
  case Nand(lhs, rhs)         => !eval(l) && !eval(r)
  case And(lhs, rhs)          => eval(Nand(Not(lhs), Not(rhs)))
  case Or(lhs, rhs)           => eval(Not(Nand(lhs, rhs)))
  case Not(e)                 => !eval(e)
  case Impl(lhs, rhs)         => eval(Not(Nand(Not(lhs), rhs)))
}

/**
Subtasks:
      
      1) Introduce a new kind of expression "Nand" (not both ... and ...).
      Eliminate And, Or, Not, and Impl by defining them as syntactic sugar for Nand.
*/

}


/**
Part 2: Binding constructs (2 subtasks, plus 1 optional subtask)
------
*/
object Hw02Part2 {
/**
Consider the language of arithmetic expressions with "with", 
as illustrated by the following abstract syntax:
*/
sealed abstract class Exp 
case class Num(n: Int) extends Exp
case class Add(lhs: Exp, rhs: Exp) extends Exp
case class Mul(lhs: Exp, rhs: Exp) extends Exp
case class Id(x: Symbol) extends Exp 
case class With(x: Symbol, xdef: Exp, body: Exp) extends Exp
 
/**
We use implicits again to make example programs less verbose. 
*/
implicit def num2exp(n: Int) = Num(n)
implicit def sym2exp(x: Symbol) = Id(x)

/**
Your task is to extend the language with the following new binding construct:
*/

case class Let(defs: List[(Symbol, Exp)], body: Exp) extends Exp

/**
The purpose of the Let construct is to bind a list of identifiers in such a way
that the scope of the bound variables is only in the body, but not any of the
right hand sides of definitions. In particular, there is no shadowing between the definitions. 
For instance, the following test case should evaluate to 7 and not to 11:
*/

val test1 = 
  With('x, 1,
   Let(List('x -> 5, 'y -> Add('x,1)),Add('x,'y)))

/**
Note: The names "Let" and "LetStar" (see below) have been choosen in analogy to the
"let" and "let*" binding constructs in Scheme and Racket.
 */

/**
Subtasks:
      
      1) Implement the missing part of the eval and subst function
      to support Let. Only change the parts currently filled with an error!
      
      2) There is some redundancy in the binding constructs of this
      language. Eliminate the construct With by defining it as
      syntactic sugar.
      
      3) Third exercise: See below.
*/

def subst(e: Exp,i: Symbol,v : Num) : Exp = e match {
  case Num(n) => e
  case Id(x) => if (x == i) v else e
  case Add(l,r) => Add( subst(l,i,v), subst(r,i,v))
  case Mul(l,r) => Mul( subst(l,i,v), subst(r,i,v))
  case With(x,xdef,body) => With(x,
                                subst(xdef,i,v),
                                if (x == i) body else subst(body,i,v))
  case Let(defs, body) =>
    val replBody = subst(body,i,v)
    val replDefs = defs map {
      //This is an anonymous function that decomposes the argument pair into x and xDef.
      case (x, xDef) => (x, subst(xDef, i, v))
    }
    Let(replDefs, replBody)
  case LetStar(defs, body) =>
    val replBody = 
    val replDefs = sys.error("not yet implemented")
    LetStar(replDefs, replBody)
}

def eval(e: Exp) : Int = e match {
  case Num(n) => n
  case Id(x) => sys.error("unbound variable: " + x.name)
  case Add(l,r) => eval(l) + eval(r)
  case Mul(l,r) => eval(l) * eval(r)
  case With(x, xdef, body) => eval(subst(body,x,Num(eval(xdef)))) 
  case Let(defs,body) => eval(subst(body,(x,(xDef,i, eval(v)))))
  case LetStar(defs,body) => sys.error("not yet implemented")
}
 
/**
Third exercise (3)
 */
/**
The LetStar construct is similar to let, but the scope of a definition contains all
right hand sides of definitions that follow the current one.
The following test case should hence evaluate to 11.   
*/

val test2 = 
     With('x, 1,
      LetStar(List('x -> 5, 'y -> Add('x,1)),Add('x,'y)))

case class LetStar(defs: List[(Symbol, Exp)], body: Exp) extends Exp

/**
Your task: Implement the missing parts of subst and eval to support LetStar.
(Again, only change the parts currently filled with an error!)
Bonus task (not mandatory): Eliminate LetStar by defining it as syntactic sugar.
 */
}
