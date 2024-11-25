# Afisare "Hello World"

.data

# Stocam sirul de caractere.
str:
.asciiz "Hello world\n"


.text
main:

    li $v0, 4	# Callcode 4  -> print_string
    la $a0, str	# $a0 -> zona de memorie de afisat
    syscall


    li $v0, 10	# Callcode 10 -> exit
    syscall
