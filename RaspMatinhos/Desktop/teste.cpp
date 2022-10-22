#include <iostream>

using namespace std;

class botao{
	
	int arquive;
	public:
		void ligar(){
		 
				cout << "Ligando..." << endl;
	};

	int value_in_gpio(int pin, int value)
	{
			arquive=0;
			char retorno[3];
			snprintf(path, 35, "/sys/class/gpio/gpio%d/value", pin);
			arquive = open(path, O_RDONLY);
			//printf("Descritor do arquivo: %d \n", arquive);
			if (arquive == -1)
			{
					return false;
			}
			if (read(arquive, retorno, 3) == -1)
			{
					close(arquive);
					return false;
			}
			close(arquive);
			printf("Valor do pino: %c \n", retorno[0]);
	 
	 
			return atoi(retorno);
	}


};


int main(int argc, char * args[]){

	botao * bt = new botao();
	
	bt->ligar();
	
	return 0;

}





	