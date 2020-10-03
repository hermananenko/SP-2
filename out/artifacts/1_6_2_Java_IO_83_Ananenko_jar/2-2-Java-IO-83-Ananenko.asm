.386
.model flat, stdcall
option casemap :none

include     C:\masm32\include\masm32rt.inc

includelib  C:\masm32\lib\masm32rt.lib

.data
.code
main proc
	mov edx, 0
	mov eax,1000
	mov ebx,100
	sub eax,ebx
	mov ebx,90
	sub eax,ebx
	mov ebx,80
	sub eax,ebx
	mov ebx,70
	sub eax,ebx
	mov ebx,60
	sub eax,ebx
	mov ebx,50
	sub eax,ebx
	mov ebx,40
	sub eax,ebx
	mov ebx,30
	sub eax,ebx
	mov ebx,20
	sub eax,ebx
	mov ebx,10
	sub eax,ebx
	mov ebx,1
	sub eax,ebx
	not edx
	neg eax
	fn MessageBoxA,0,str$(eax),"1_6-2-Java-IO-83-Ananenko",MB_OK
	ret
main endp
start:
	invoke main
	invoke ExitProcess,0
end start