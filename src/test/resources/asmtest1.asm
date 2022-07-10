ORG 0x4000
;-----------------------------------------------------;
;             VTL-2 for the 6502 (VTL02B)             ;
;           Original Altair 680b version by           ;
;-----------------------------------------------------;
  LDC #$08
LABEL decrement
  DEC C
  STC $0200
  CPC #$03
  JPNZ decrement
  STC $0201
  BRK

JMP end
LABEL start
JSNZ #$04
JMP start
JSRZ $FF
LDA B
LABEL end

DATA "this~is~a~String"
; DATA 0B00000000
; DATA 0B00011000
; DATA 0B01100110
; DATA 0B01111110
; DATA 0B01100110
; DATA 0B01100110
; DATA 0B00000000
