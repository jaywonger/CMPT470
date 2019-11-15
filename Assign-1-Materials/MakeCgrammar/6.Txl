%Grammer:
define program
  [c_function]
end define

define c_function
  [function_head] [NL]
  { [NL][IN]
    [function_body]
  }
end define

define function_head
  [return_type] [function_name]()
end define

define function_name
  [id]
end define

define function_body
  [repeat variable_declaration] [repeat assignExpr] [return_statement]
  | [repeat variable_declaration] [return_statement]
  | [print_statement] [return_statement]
end define

define return_type
  int | char
end define

define endLine %the '; is optional in condition statements
  ';
end define

define assignExpr
  [variable_declaration] [assignOp] [expression]; [NL]
  | [variable_declaration] [assignOp] [charExpression]; [NL]
end define

compounds
  == :=
end compounds

define assignOp
  = | == | :=
end define

define variable_declaration
  [repeat id]
  | [repeat id]; [NL]
  | [id] [id],[id],[id]; [NL]
end define

define charExpression
  [charlit]
  |[charExpression] '+ [charExpression]
end define

define expression
  [id] | [number]
end define

define print_statement
  printf([stringlit]); [NL]
end define

define return_statement
  return [return_value]; [NL][EX]
end define

define return_value
  [number]
end define

% Rules:
function main
    match [program]
  P[program]
end function
