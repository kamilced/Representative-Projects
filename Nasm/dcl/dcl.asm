%macro print 1
        mov     rax, 1
        mov     rdi, 1
        mov     rsi, %1
        mov     rdx, 1
        syscall
%endmacro
; Macro which reverse permutation passed in the first argument, and saves it in the second one.
; It also checks if signs in permutation are in proper range and proper amount.
%macro reverse_perm 2
        mov     r12b, FIRST_SIGN
        xor     rcx, rcx
%%loop:
        ; cmp     rcx, SIGNS_NUM
        ; je      end_of_loop
        movzx   eax, byte[%1 + rcx]
        cmp     eax, 0
        je      %%end_of_loop
        cmp     eax, FIRST_SIGN   ; checks if sign is in proper range
        jl      wrong_args
        cmp     eax, LAST_SIGN
        jg      wrong_args

        cmp     byte [%2 + rax - FIRST_SIGN], 0 ; checks if sign didn't appear already
        jne     wrong_args

        mov     byte [%2 + rax - FIRST_SIGN], r12b
        inc     r12b
        inc     rcx
        jmp     %%loop

%%end_of_loop:
        cmp     rcx, SIGNS_NUM
        jne     wrong_args
%endmacro

; macro takes char which has to be moved, and two numbers, first when we need to add to get the result and second when substract is needed
%macro perm_Q 3
        cmp     %1 + %2, LAST_SIGN
        jg      substract
        mov     %1, %1 + %2

substract:
        mov     %1, %1 - %3
%endmacro

section .data
        BUFFER_SIZE equ 10
        SIGNS_NUM   equ 42
        FIRST_SIGN  equ '1'
        LAST_SIGN   equ 'Z'
        reversed_L: times SIGNS_NUM db 0
        reversed_T: times SIGNS_NUM db 0
        reversed_R: times SIGNS_NUM db 0

section .bss
        
        ibuffer resb BUFFER_SIZE
        obuffer resb BUFFER_SIZE
        add_l   resb 1
        sub_l   resb 1
        add_r   resb 1
        sub_r   resb 1

section .text
global _start
_start:
        pop     rcx
        cmp     rcx, 5
        jne     wrong_args

        
        mov     rbx, [rsp + 8]        ; L permutation
        reverse_perm rbx, reversed_L
        mov     rdi, [rsp + 16]       ; R permutation
        reverse_perm rdi, reversed_R
        mov     rdx, [rsp + 24]       ; T permutation
        reverse_perm rdx, reversed_T
        mov     r8, [rsp + 32]
        mov     r9b, byte[r8]
        mov     [add_l], r9b
        
        mov     eax, FIRST_SIGN
        sub     [add_l], eax
        mov     [sub_l], byte [SIGNS_NUM - add_l]
        
        print   add_l
        
        ; print   reversed_T

       ; perm_Q msg, 


        ;print reversed_L

        ; first attempt of reading data which had to be transformed
        ; mov     eax, 3
        ; mov     ebx, 0
        ; mov     ecx, ibuffer
        ; mov     edx, BUFFER_SIZE
        ; syscall
        ; mov rbx, [rsp + 8]
        ;print rbx


        ;     ; L permutation
        ; movzx   eax, byte [ibuffer + rcx] ; in rax sign to permute
        ; movzx   eax, byte [rbx + rax - '1'] ; permutation is done
        ; print   rax

        ; ;mov     rcx, [rsp + 16]   ; R
        ; ;mov     rdx, [rsp + 24]   ; T
        ; mov     r10, [rsp + 32]   ; key
        
        
        
        ; movzx     r8, byte [r10]        ; l
        ; ; mov     r9b, [r10 + 1]    ; r
        
        ; print r8
        jmp     exit

wrong_args:
        ;print reversed_L
        mov     rax, 60
        mov     rdi, 1
        syscall

exit:
        mov     rax, 60
        mov     rdi, 0
        syscall