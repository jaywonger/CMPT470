%Grammer:
#pragma -id chars "/,"

define program
  [c_function]
end define

define c_function
  [function_head] [NL]
  { [NL][IN]
    [function_body] [return_statement]
  }
end define

define function_head
  [return_type] [function_name]()
end define

define function_name
  [id]
end define

define function_body
  [repeat command]
end define

define command
  [if_else_statement]
  | [if_else_statement2]
  | [repeat variable_declaration] [repeat assignExpr]
  | [print_statement]
  | [scanf_statement]
end define

define return_type
  int | char
end define

define endLine %the '; is optional in condition statements
  ';
end define

define bracket  % bracket definition
  '( | ')
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
  | [id],
end define

define charExpression
  [charlit]
  | [charExpression] [op] [charExpression]
  | [charExpression] [op] [expression]
end define

define expression
  [id] | [number] | [expression] [op] [expression]
end define

define op
  '+ | '- | '* | '> | '<
end define

define bit_AND
  '&
end define

define comma
  ',
end define

define if_else_statement
  if([expression]) [NL] [IN]
    [expression] [EX]
  | if([expression]) [NL] [IN]
      [expression] [EX]
    else [NL] [IN]
      [expression] [EX]
end define

define if_else_statement2
  if([expression]) [NL]
  { [NL] [IN]
    [repeat command] [EX]
  } [NL]
    else [NL]
    { [NL] [IN]
      [repeat command] [EX]
    } [NL]
end define

define scanf_statement
  scanf([stringlit],[bit_AND][expression]); [NL]
end define

define print_statement
  printf([stringlit]); [NL] |
  printf([stringlit],[expression]); [NL]
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
