/*
 *************************************************************
 **** molcompress.c - version 2.6
 **** Molfile Compression/Decompression routines
 ****
 **** Copyright (c) 1998-2012 ChemAxon Ltd., Peter Csizmadia
 **** All Rights Reserved.
 ****
 **** This software is free for noncommercial use.
 *************************************************************
 */

/*
 * USAGE:
 * Compression of a molfile:
 * char[] mol = "";
 * char* csmol = molCompress(mol
 */

#include <stdio.h> /* sprintf, sscanf */
#include <stdlib.h> /* malloc */
#include <string.h>

static char* atoms[128] = {
"   ","H  ","He ", //1-2
"Li ","Be ","B  ","C  ","N  ","O  ","F  ","Ne ", /*3-10*/
"Na ","Mg ","Al ","Si ","P  ","S  ","Cl ","Ar ", /*11-18*/
"K  ","Ca ","Sc ","Ti ","V  ","Cr ","Mn ","Fe ", /*19-26*/
"Co ","Ni ","Cu ","Zn ","Ga ","Ge ","As ","Se ","Br ","Kr ", /*27-36*/
"Rb ","Sr ","Y  ","Zr ","Nb ","Mo ","Tc ","Ru ", /*37-44*/
"Rh ","Pd ","Ag ","Cd ","In ","Sn ","Sb ","Te ","I  ","Xe ", /*45-54*/
"Cs ","Ba ","La ","Ce ","Pr ","Nd ","Pm ","Sm ", /*55-62*/
"Eu ","Gd ","Tb ","Dy ","Ho ","Er ","Tm ","Yb ","Lu ", /*63-71*/
"Hf ","Ta ","W  ","Re ","Os ","Ir ","Pt ","Au ", /*72-79*/
"Hg ","Tl ","Pb ","Bi ","Po ","At ","Rn ", /*80-86*/
"Fr ","Ra ","Ac ","Th ","Pa ","U  ","Np ","Pu ", /*87-94*/
"Am ","Cm ","Bk ","Cf ","Es ","Fm ","Md ","No ","Lr ", /*95-103*/
"Rf ","Db ","Sg ","Bh ","Hs ","Mt ", /*104-109*/
"L  ","LP ","A  ","Q  ","*  ","R# ", /*110-115*/
(char*)0, (char*)0, (char*)0, (char*)0, (char*)0, (char*)0, (char*)0,
(char*)0, (char*)0, (char*)0, (char*)0, (char*)0}; /*116-127*/

static int getatno(char* s)
{
    int i;
    for(i=0; atoms[i]!=(char*)0; ++i)
	if(!strcmp(atoms[i], s))
	    return i;
    return 0;
}

#ifdef __cplusplus
#define ALLOC(n)  new char[n]
#define FREE(x)   delete x
#else
#define ALLOC(n)  malloc(n)
#define FREE(x)   free(x)
#endif

/***********************
 **** compression only
 ***********************/

static char* digits64 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz+-";

/* Convert an integer into reverse base-64. */
static void mito6(char* s, int offset, int i, int n)
{
    int j;
    for(j=0; j<n; ++j) {
	int k = i&63;
	s[offset+j] = digits64[k];
	i >>= 6;
    }
    s[offset+n] = '\0';
}

/***********************
 **** decompression only
 ***********************/

static int digits64u[128] = {
0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0, /*0-31*/
0,0,0,0,0,0,0,0,0,0,0,62,0,63,0,0,0,1,2,3,4,5,6,7,8,9,0,0,0,0,0,0,0, /*32-64*/
10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,
0,0,0,0,0,0, /*65-96*/
36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,
0,0,0,0,0}; /*97-127*/

/* Read reverse base-64 integer. */
static int m6toi(char* s, int l, int m)
{
    int i = 0;
    int j;
    for(j=l; j<m; ++j) {
	int c = s[j];
	i |= digits64u[c] << ((j-l)*6);
    }
    return i;
}

/*
 * Count lines.
 */
static int countlines(const char* s) {
    int n = 0;
    int len = 0;
    char* p;
    for(p=(char*)s; *p!='\0'; ++p) {
	if(*p == '\n') {
	    ++n;
	} else if(*p == '\r' && *(p+1) != '\n') {
	    if(p == (char*)s) {
		++n;
	    } else if(*(p-1) != '\n') {
		++n;
	    }
	}
	++len;
    }
    if(len > 0)
	if(s[len-1] != '\n' && s[len-1] != '\r')
	    ++n;
    return n;
}

/*
 * Make array of lines.
 */
static char** mklinearr(const char* s) {
    int i;
    int n = countlines(s);
    char* p = (char*) s;
    char** M = (char**)ALLOC(sizeof(char*)*(n+3));
    for(i=0; i<n; ++i) {
	int j;
	for(j=0; p[j]!='\n' && p[j]!='\r' && p[j]!='\0'; ++j);
	M[i] = (char*)ALLOC(sizeof(char)*(j+1));
	memcpy(M[i], p, j);
	M[i][j] = '\0';
	p += j;
	if((p[0]=='\n' && p[1]=='\r') || (p[0]=='\r' && p[1]=='\n'))
	    ++p;
	++p;
    }
    M[n] = (char*)0;
    return M;
}

/*
 * Destruct array of lines.
 */
static void freelinearr(char** M) {
    char** p;
    for(p=M; *p!=(char*)0; ++p) {
	FREE(*p);
    }
    FREE(M);
}

/*
 * Create array of charges from the "M  CHG" field.
 */
static int* mkchg(char** M, int na, int nb, int* nchg)
{
    int i;
    int* chg = (int*)ALLOC(sizeof(int)*(na+1));
    memset(chg, 0, sizeof(int)*(na+1));
    *nchg = 0;
    for(i=4+na+nb; M[i]!=(char*)0; ++i) {
	int len = strlen(M[i]);
	if(len > 9) {
	    int ismchg = !memcmp(M[i], "M  CHG", 6*sizeof(char));
	    int n = 0;
	    sscanf(M[i]+6, "%3d", &n);
	    if(ismchg && n > 0 && len >= 9+8*n) {
		int j;
		for(j=0; j<n; ++j) {
		    int k = 9+8*j;
		    int a = 0;
		    int charge = 0;
		    sscanf(M[i]+k, "%4d%4d", &a, &charge);
		    --a;
		    if(a>=0 && a<na && charge!=0) {
			chg[a] = charge;
			++(*nchg);
		    }
		}
	    }
	}
    }
    return chg;
}

/*
 * Create array of radicals from the "M  RAD" field.
 */
static int* mkrad(char** M, int na, int nb, int* nrad)
{
    int i;
    int* rad = (int*)ALLOC(sizeof(int)*(na+1));
    memset(rad, 0, sizeof(int)*(na+1));
    *nrad = 0;
    for(i=4+na+nb; M[i]!=(char*)0; ++i) {
	int len = strlen(M[i]);
	if(len > 9) {
	    int ismrad = !memcmp(M[i], "M  RAD", 6*sizeof(char));
	    int n = 0;
	    sscanf(M[i]+6, "%3d", &n);
	    if(ismrad && n > 0 && len >= 9+8*n) {
		int j;
		for(j=0; j<n; ++j) {
		    int k = 9+8*j;
		    int a = 0;
		    int radical = 0;
		    sscanf(M[i]+k, "%4d%4d", &a, &radical);
		    --a;
		    if(a>=0 && a<na && radical!=0) {
			rad[a] = radical;
			++(*nrad);
		    }
		}
	    }
	}
    }
    return rad;
}

/****************************************
 ******* Compression/Decompression ******
 ****************************************/

char* molCompress(const char* str, int compress)
{
    char tmps[100];
    char* outstr;
    int outlen = 0;
    int nlines = countlines(str);
    char** M = mklinearr(str);
    int na, nb, nchg, hasmchg, nrad, hasmrad;
    double A = 10000.0;
    double B = 8388608.0;
    int* chg;
    int* rad;
    int i;
    if(nlines < 5) { /* bad format */
	freelinearr(M);
	return (char*)0;
    }
    sscanf(M[3], "%3d%3d", &na, &nb);
    if(na < 0)
	na = 0;
    if(nb < 0)
	nb = 0;
    if(4+na+nb >= nlines) { /* bad format */
	freelinearr(M);
	return (char*)0;
    }
    chg = mkchg(M, na, nb, &nchg);
    rad = mkrad(M, na, nb, &nrad);
    hasmchg = nchg != 0;
    hasmrad = nrad != 0;

    /* atom block */
    for(i=0; i<na; ++i) {
	char* s = M[4+i];
	int len = strlen(s);
	if(len == 10 || len == 14) { /* compressed line */
	    if(!compress) {
		double x = (m6toi(s, 0, 4)-B)/A;
		double y = (m6toi(s, 4, 8)-B)/A;
		int offset = 8;
		int a, stereo;
		char* atsym;
		int chargecode = 0;
		sprintf(tmps, "%10.4f%10.4f", x, y);
		if(len == 14) { /* 3d */
		    double z = (m6toi(s, 8, 12)-B)/A;
		    sprintf(tmps+20, "%10.4f ", z);
		    offset += 4;
		} else { /* 2d */
		    strcpy(tmps+20, "    0.0000 ");
		}
		a = m6toi(s, offset, offset+2);
		atsym = atoms[a&127];
		stereo = (a>>7)&3;
		if(chg[i] != 0) {
		    chargecode = 4-chg[i];
		} else if(rad[i] == 2) {
		    chargecode = 4;
		}
		sprintf(tmps+31, "%3s%2d%3d%3d  0  0  0  0  0  0  0  0  0",
			atsym, 0, chargecode, stereo);
		FREE(M[4+i]);
		M[4+i] = (char*)ALLOC(sizeof(char)*(strlen(tmps)+1));
		strcpy(M[4+i], tmps);
	    }
	} else { /* uncompressed line */
	    if(compress && len >= 42) {
		int unknown = 0;
		double xx, yy;
		int x, y;
		char atsym[4];
		int a, atno, chargecode, stereo;
		int offset = 8;
		sscanf(s, "%10lf%10lf", &xx, &yy);
		memcpy(atsym, s+31, 3*sizeof(char));
		atsym[3] = '\0';
		sscanf(s+36, "%3d%3d", &chargecode, &stereo);
		x = (int)(xx*A+B+0.5);
		y = (int)(yy*A+B+0.5);
		mito6(tmps, 0, x, 4);
		mito6(tmps, 4, y, 4);
		if(memcmp(M[4+i]+20, "    0.0000", 10*sizeof(char))) {
		    double zz;
		    int z;
		    sscanf(s+20, "%10lf", &zz);
		    z = (int)(zz*A+B+0.5);
		    mito6(tmps, 8, z, 4);
		    offset += 4;
		}
		if((atno = getatno(atsym)) <= 0) { /* unknown atom type */
		    atno = 0;
		    unknown = 1;
		}
		if(stereo < 0 || stereo > 3) {
		    unknown = 1;
		}
		a = atno & 127; /* 7 bit */
		a |= (stereo & 3) << 7; /* 2 bit */
		mito6(tmps, offset, a, 2);
		if(chargecode == 4) {
		    if(rad[i] == 0) {
			++nrad;
			rad[i] = 2;
		    }
		} else if(chargecode != 0 && chg[i] == 0) {
		    ++nchg;
		    chg[i] = 4 - chargecode;
		}

		/* line containing unknown fields cannot be compressed */
		if(memcmp(s+34, " 0", 2*sizeof(char))) {
		    unknown = 1;
		} else if(!unknown) {
		    int j;
		    for(j=42; j<len; ++j) {
			char c = s[j];
			if(c != ' ' && c != '0') {
			    unknown = 1;
			    break;
			}
		    }
		}
		if(!unknown) {
		    FREE(M[4+i]);
		    M[4+i] = (char*)ALLOC(sizeof(char)*(strlen(tmps)+1));
		    strcpy(M[4+i], tmps);
		}
	    }
	}
    }

    /* bond block */
    for(i=0; i<nb; ++i) {
	char* s = M[4+na+i];
	int len = strlen(s);
	if(len == 5) { /* compressed line */
	    if(!compress) {
		int i1 = m6toi(s,0,2);
		int i2 = m6toi(s,2,4);
		int t = m6toi(s,4,5);
		int type = t & 7;
		int stereo = (t>>3) & 7;
		if(type == 0) {
		    type = 8;
		}
		sprintf(tmps, "%3d%3d%3d%3d  0  0  0", i1, i2, type, stereo);
		FREE(M[4+na+i]);
		M[4+na+i] = (char*)ALLOC(sizeof(char)*(strlen(tmps)+1));
		strcpy(M[4+na+i], tmps);
	    }
	} else { /* uncompressed line */
	    if(compress && len >= 12) {
		int unknown = 0;
		int i1 = 0;
		int i2 = 0;
		int t;
		int type = 0;
		int stereo = 0;
		sscanf(s, "%3d%3d%3d%3d", &i1, &i2, &type, &stereo);
		if(type<1 || type>8 || stereo!=(stereo&7)) {
		    unknown = 1;
		}
		stereo &= 7;
		t = (type & 7) | (stereo << 3);
		mito6(tmps, 0, i1, 2);
		mito6(tmps, 2, i2, 2);
		mito6(tmps, 4, t, 1);

		/* line containing unknown fields cannot be compressed */
		if(!unknown) {
		    int j;
		    for(j=12; j<len; ++j) {
			char c = s[j];
			if(c != ' ' && c != '0') {
			    unknown = 1;
			    break;
			}
		    }
		}
		if(!unknown) {
		    FREE(M[4+na+i]);
		    M[4+na+i] = (char*)ALLOC(sizeof(char)*(strlen(tmps)+1));
		    strcpy(M[4+na+i], tmps);
		}
	    }
	}
    }

    /* properties block */
    if(nchg != 0 && !hasmchg) {
	char* sc = (char*)ALLOC(10+6*nb);
	int offset = 9;
	memmove(M+4+na+nb+1, M+4+na+nb, sizeof(char*)*(nlines-3-na-nb));
	sprintf(sc, "M  CHG%3d", nchg);
	for(i=0; i<na; ++i) {
	    if(chg[i] != 0) {
		sprintf(sc+offset, "%3d%3d", i+1, chg[i]);
		offset += 6;
	    }
	}
	M[4+na+nb] = sc;
    }
    if(nrad != 0 && !hasmrad) {
	char* sc = (char*)ALLOC(10+6*nb);
	int offset = 9;
	memmove(M+4+na+nb+1, M+4+na+nb, sizeof(char*)*(nlines-3-na-nb));
	sprintf(sc, "M  RAD%3d", nrad);
	for(i=0; i<na; ++i) {
	    if(rad[i] != 0) {
		sprintf(sc+offset, "%3d%3d", i+1, rad[i]);
		offset += 6;
	    }
	}
	M[4+na+nb] = sc;
    }

    /* clean up */
    FREE(chg);
    FREE(rad);

    /* make output string */
    for(i=0; M[i]!=(char*)0; ++i) {
	outlen += strlen(M[i])+1;
    }
    outstr = (char*)ALLOC(sizeof(char)*(outlen+1));
    outstr[0] = '\0';
    for(i=0; M[i]!=(char*)0; ++i) {
	strcat(outstr, M[i]);
	strcat(outstr, "\n");
    }

    /* clean up */
    freelinearr(M);

    return outstr;
}

#ifdef MAIN
int main(int argc, char* argv[])
{
    int i;
    char buf[65536];
    FILE* f = fopen(argv[2], "r");
    if(f != NULL) {
	int c;
	char* p = buf;
	while((c = fgetc(f)) != -1)
	    *(p++) = c;
	*p = 0;
	fputs(molCompress(buf, argv[1][0] == 'c'), stdout);
    }
}
#endif
