## N Route Simulator
O projeto tem como objetivo fornecer uma implementação abstrata de um roteador e um switch, permitindo simular uma rede IP com funcionalidades básicas, como roteamento, ping e traceroute. No momento, não há suporte para protocolos complexos como BGP, OSPF, entre outros. A ideia central é fornecer uma simulação simples com um roteador básico e um switch de camada 2. Funcionalidades como suporte a VLANs e VRFs estão previstas, mas ainda não foram implementadas.

### NRouter - Router Simulator
A classe NRouter representa a simulação de um roteador. Abaixo, um exemplo de como criar um roteador com 4 interfaces:

```java
....
import dev.nishisan.ip.router.ne.NRouter;
....

public class SimpleRouterExample {

    public static void main(String[] args) {
        /**
         * Create a Router with 4 Interfaces
         */
        NRouter router1 = new NRouter("router-1");
        router1.addInterface("ge0/0/0/1", "192.168.0.1/24", "UPLINK"); // Interface com Descrição
        router1.addInterface("ge0/0/0/2", "192.168.1.1/24");           // Só a interface com ip
        router1.addInterface("ge0/0/0/3", "192.168.2.1/24");
        router1.addInterface("ge0/0/0/4", "192.168.3.1/24");
    }
}
```
### Exibindo as interfaces
Para visualizar as interfaces do roteador, simulando um comando equivalente ao show int desc, você pode utilizar o método printInterfaces(). Exemplo:


```java
NRouter router1 = new NRouter("router-1");
router1.addInterface("ge0/0/0/1", "192.168.0.1/24", "UPLINK");
router1.addInterface("ge0/0/0/2", "192.168.1.1/24");
router1.addInterface("ge0/0/0/3", "192.168.2.1/24");
router1.addInterface("ge0/0/0/4", "192.168.3.1/24");
router1.printInterfaces();
```

O código acima gerará a seguinte saída:

```
------------------------------------------------------------------------------------
Device.........:[router-1]
Interfaces.....:[4]
------------------------------------------------------------------------------------
Interface       Admin Status    Oper Status     MAC Address        Description                   
------------------------------------------------------------------------------------
ge0/0/0/1       ADMIN_UP        OPER_DOWN       F8:FC:D3:E4:57:79  UPLINK                        
ge0/0/0/2       ADMIN_UP        OPER_DOWN       DC:31:22:BB:C7:2A                                
ge0/0/0/3       ADMIN_UP        OPER_DOWN       DA:EC:64:4A:05:26                                
ge0/0/0/4       ADMIN_UP        OPER_DOWN       4C:95:2A:3A:00:82                                
------------------------------------------------------------------------------------
```

### Injetando Rotas no Roteador
Para injetar rotas no roteador o que precisavmos fazer é utilizar o método addRouteEntry();
O método pede os seguintes parâmetros:

| Parametro | Tipo | Descrição                                                                                                           |
|-----------|------|---------------------------------------------------------------------------------------------------------------------|
| dst | String | Bloco rede ipv4 de destino. 0.0.0.0/0 para Default GW                                                               |
| nextHop | String | Endereço  ipv4 do NextHop, se for null, a interface é obrigatória.                                                  |
| src | String | Endereço  ipv4 do ip a ser usado como source.Se omitido considera o ip da interface                                 |
| dev | NRouterInterface | A interface de rede que será usada para o nexthop, pode ser obtida através do nome pelo método getInterfaceByName() |

Exemplo de uso tabela de roteamento, assumindo que router1 é uma instância de NRouter:
```java
//
// Exemplo abaixo adiciona a rota padrão por 192.168.0.254 através da interface ge0/0/0/1
//
router1.addRouteEntry("0.0.0.0", "192.168.0.254", "192.168.0.1", router1.getInterfaceByName("ge0/0/0/1")); // Default GW

//
// Este exemplo adiciona uma rota especifica para destino 192.168.8.1/32 pelo GW: 192.168.3.254
//
router1.addRouteEntry("192.168.8.1/32", "192.168.3.254");
```

### Obtendo o nexthop 
Uma vez que sua tabela de roteamento está montada você pode obter o nexthop através do seguinte método.