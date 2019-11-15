int main()
{
  int i,j,mark;
  char pass;
  for (i = 1;i <= 24;i++)
  {
    if ( i < 10)
    {
      printf("H\n");
      printf("testing\n");
    }
  }
  scanf("%d",&mark);
  if (mark > 40)
  {
    if (mark > 90)
    {
      printf("You are really smart!");
      printf("oh yah, for sure");
    }
    pass = 'y';
    printf("You passed");
  }
  else
  {
    for (i = 0; i < 3; i++)
    {
      printf("FAILure!");
    }
    pass = 'n';
    printf("You failed");
  }
  printf("end of code");
  return 0;
}
