package models

case class Registration(
  email           : String,
  password        : String,
  retypedPassword : String
)