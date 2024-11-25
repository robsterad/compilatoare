# Pseudocod
# print_string("1");
# if N > 64 then
#     print_string("Large value")
# else
#     print_string("Small value")
# end-if
# print_string("2");

.data

# Stocam cele 4 siruri de caractere din pseudocod.

string_one:
.asciiz "1\n"

string_two:
.asciiz "2\n"

string_large:
.asciiz "Large value\n"

string_small:
.asciiz "Small value\n"

.text
main:

# Variabila N o stocam in registrul v1.
    li $v1, 5

# print_string("1");
    li $v0, 4			# Callcode 4  -> print_string
    la $a0, string_one	# $a0 -> zona de memorie de afisat
    syscall


# Pentru a implementa un if-then-else sunt necesare 2 etichete.
# O eticheta pentru ramura else.
# Eticheta este folosita pentru a sari peste ramura then daca conditia este falsa.
# O eticheta pentru sfarsitul intregii constructii if-then-else
# Eticheta este folosita pentru a sari peste ramura else atunci cand se termina de executat ramura then.

    ble $v1, 64, small	# Verificam conditia si sarim peste ramura then daca conditia este falsa.

# print_string("Large value")		ramura then a if-ului
    li $v0, 4
    la $a0, string_large
    syscall
    j end	# Salt neconditionat la eticheta "end", eticheta plasata pentru a sari peste ramura else.

# print_string("Small value")		ramura else a if-ului
small:
    li $v0, 4
    la $a0, string_small
    syscall

# print_string("2");
end:
    li $v0, 4
    la $a0, string_two
    syscall

    li $v0, 10			# Callcode 10 -> exit
    syscall