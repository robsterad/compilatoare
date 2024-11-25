# Pseudocod
# var = 12
# print_int(var)
# while(var != 1)
#     if (var mod 2 == 0)
#         var = var / 2
#     else
#         var = 3 * var + 1
#     print_string(",")
#     print_int(var)


.data

# Stocam sirul de caractere cu virgula.
str:
.asciiz ","

var:
.word 27

.text
main:

    lw $v1, var

    li $v0, 1				# Callcode 1  -> print_int.
    move $a0, $v1			# $a0 -> numarul de afisat
    syscall

loop:
    beq $v1, 1, end

    li $t0, 2
    divu $v1, $t0
    mfhi $t1				# $t1 devine restul impartirii lui $v1 la 2
    bne $t1, 0, elsebranch
    mflo $v1				# $v1 devine catul impartirii lui $v1 la 2
    j afterif

elsebranch:
    li $t0, 3
    mult $v1, $t0
    mflo $v1
    addiu $v1, $v1, 1

afterif:

    li $v0, 4				# Callcode 4  -> print_string
    la $a0, str				# $a0 -> zona de memorie de afisat
    syscall

    li $v0, 1				# Callcode 1  -> print_int.
    move $a0, $v1			# $a0 -> numarul de afisat
    syscall

    j loop

end:
    li $v0, 10				# Callcode 10 -> exit
    syscall