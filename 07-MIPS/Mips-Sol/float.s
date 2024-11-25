# Media unui vector de floats
# Media se va afisa atat cu zecimale cat si fara zecimale

.data

# Aici sunt float-urile cu care vom lucra
float_values:
.float 1.1, 2.2, 3.14, 4.4, 1.9

# Retinem marimea vectorului de float-uri in octeti
float_values_size:
.word 20   # 5 float-uri * 4 octeti = 20 octeti

newline_string:
.asciiz "\n"

.text
main:

# In f0 vom retine suma tuturor valorilor.

    lw $v1, float_values_size	# Retinem capatul unde ne vom opri
    li $v0, 0					# Retinem indicele for-ului
    li.s $f0, 0.0				# Initializam $f0

add_loop:
    beq $v0, $v1, end_loop		# Oprim bucla daca am ajuns la capat
    lwc1 $f1, float_values($v0)	# Incarcam in f1 de la adresa float_values + $v0
    add.s $f0, $f0, $f1			# Adunam in acumulator
    addiu $v0, $v0, 4			# i <- i + 4
    j add_loop

end_loop:

    lw $t1, float_values_size	# Incarcam numarul de octeti al sirului
    srl $t1, $t1, 2				# Numarul de float-uri = numarul de octeti / 4 = numarul de octeti >> 2
    mtc1 $t1, $f1				# Copiem $t1 in $f1 (instructiuni speciale pentru mutat de la registrele procesorului la registrele coprocesorului de float)
    cvt.s.w $f1, $f1			# Convertim din valoare intreaga in valoare float
    div.s $f0, $f0, $f1


    mov.s $f12, $f0
    li $v0, 2           		# Callcode 2  -> print_float
    syscall						# $f12 -> numarul de afisat

    li $v0, 4					# Callcode 4  -> print_string
    la $a0, newline_string		# $a0 -> zona de memorie de afisat
    syscall

    cvt.w.s $f12, $f12  		# Convertim float-ul $f12 in numar intreg

    mfc1 $a0, $f12				# Mutam in $a0
    li $v0, 1           		# Callcode 1  -> print_int.
    syscall             		# $a0 -> numarul de afisat

    li $v0, 10					# Callcode 10 -> exit
    syscall