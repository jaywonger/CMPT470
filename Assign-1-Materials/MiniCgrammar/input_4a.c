#include<stdio.h>

int main()
{
  int a,b,c;
  int count;
  a = 0;
  b = 0;
  c = 0;

  scanf("d", &a);
  scanf("d", &b);
  scanf("d", &c);
  scanf("d", &count);

  while(a <= count)
  {
    while(b <= count)
    {
      while(c <= count)
      {
        printf("%c\n", c);
        printf("made it to c");
        c++;
      }
      printf("%b\n", b);
      printf("made it to b");
      b++;
    }
    printf("%a\n", a);
    printf("made it to a");
    a++;
  }
  return 0;
}
