
define program
   [assgnExpr]
end define


function main
   match [program]
   P[program]
end function 


compounds
 == :=
end compounds

define bool_op
 '==
end define

define assgn_op
 ':=
end define

define assgn_bool_op
 [bool_op]
 | [assgn_op] 
end define


define assgnExpr
 [id] [assgn_bool_op] [expression]
end define

define ID_or_Number
     [id]
    | [number]
end define

define expression
  [ID_or_Number]
 |[expression] '+ [expression]
 |[expression] '- [expression]
 |[expression] '/ [expression]
 |[expression] '* [expression]
end define

define op
 '+
 |'-
 |'/
 |'*
end define


