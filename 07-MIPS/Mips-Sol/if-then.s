# Pseudocod
# print_string("1");
# if N > 64 then
#     print_string("Large value")
# end-if
# print_string("2");

.data

# Stocam cele 3 siruri de caractere din pseudocod.

string_one:
.asciiz "1\n"

string_two:
.asciiz "2\n"

string_text:
.asciiz "Large value\n"

.text
main:


# Variabila N o stocam in registrul $v1.
    li $v1, 5

# print_string("1");
    li $v0, 4			# Callcode 4 -> print_string
    la $a0, string_one	# $a0 -> zona de memorie de afisat
    syscall

# Verificam conditia din if si sarim peste ramura then daca conditia este falsa.
    ble $v1, 64, end 	# salt la end daca $v1 <= 64

# print_string("Large value")      ramura then a if-ului
    li $v0, 4
    la $a0, string_text
    syscall

end:
# print_string("2");
    li $v0, 4
    la $a0, string_two
    syscall

    li $v0, 10			# Callcode 10 -> exit
    syscall