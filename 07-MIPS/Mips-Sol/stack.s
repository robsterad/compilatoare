# Incarca pe stiva numerele de la 20 la 1 si pe urma numerele sunt scoase din stiva in ordine si afisate.

.text
main:

# Adaugare numere pe stiva

# Adaugam un zero pe stiva pentru a-l gasi cand scoatem numerele in ordine.

    li $v1, 0
    addiu $sp, $sp, -4   		# Alocam spatiu pe stiva pentru 4 octeti (32 de biti). Stiva creste in jos (inspre 0).
    sw $v1, 0($sp)				# Spatiul nou alocat este de la $sp la $sp + 4.

    li $v1, 20 # contorul

start_push:
    beq $v1, 0, end_push		# $v1 == 0 => salt la end_push
    addiu $sp, $sp, -4   		# Alocam spatiu pe stiva pentru 4 octeti (32 de biti). Stiva creste in jos (inspre 0).
    sw $v1, 0($sp)       		# Spatiul nou alocat este de la $sp la $sp + 4.
    addiu $v1, $v1, -1    		# Decrementam contorul
    j start_push				# Inapoi la bucla

end_push:

start_pop:
    ld $a0, 0($sp)       		# Valoare pe stiva ocupa memorie de la $sp la $sp + 4. Incarcam in $a0.
    addiu $sp, $sp, 4    		# Dealocam spatiu pe stiva pentru 4 octeti (32 de biti). Stiva scade in sus (inspre numere mari).
    beq $a0, $zero, end   		# Oprim daca am intalnit zero
    li $v0, 1            		# Callcode 1  -> print_int.
    syscall              		# $a0 -> numarul de afisat
    b start_pop

end:
    li $v0, 10					# Callcode 10 -> exit
    syscall