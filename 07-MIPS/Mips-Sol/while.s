# Pseudocod
# unsigned int i = 20;
# while(i > 0)
#     print_int(i)
#     i--

.text
main:

    li $v1, 20			# contorul i

#  while (i > 0)
start:
    beq $v1, 0, end		# Oprim bucla cand i ajunge 0

#  print_int(i);
    li $v0, 1			# Callcode 1  -> print_int.
    move $a0, $v1		# $a0 -> numarul de afisat
    syscall


    addiu $v1, $v1, -1	# i--
    j start

end:
    li $v0, 10		    # Callcode 10 -> exit
    syscall