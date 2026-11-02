![senai_logo](./src/image/logo.png)

# ScreenSaver – Projeto Base para Programação Orientada a Objetos

## Objetivo

Este projeto tem como objetivo servir de base para o estudo dos pilares da Programação Orientada a Objetos (POO): **encapsulamento, abstração, herança e polimorfismo**.

A aplicação consiste em um protetor de tela simples utilizando Java Swing. A classe `ScreenSaver` fornece toda a infraestrutura da aplicação (janela, painel, timer, renderização e captura do teclado). O desenvolvimento do projeto concentra-se na implementação das classes do pacote `model` e na integração dessas classes com os pontos demarcados em `ScreenSaver.java`.

## Estrutura do Projeto

```mermaid
treeView-beta
src
├── App.java
├── model
│   ├── Forma.java
│   ├── Retangulo.java
│   ├── Circulo.java
│   └── ...
└── view
    └── ScreenSaver.java
```

## Localizando os pontos de implementação

### 1. Declaração das formas

```java
/*********************************
** ↓ Declare suas formas aqui ↓ **
*********************************/
```

Declare todos os atributos das formas.

```java
private Retangulo jogador;
private Circulo bola;
```

### 2. Instanciação das formas

```java
/***********************************
** ↓ Instancie suas formas aqui ↓ **
***********************************/
```

Crie os objetos.

```java
jogador = new Retangulo(...);
bola = new Circulo(...);
```

### 3. Movimentação das formas

```java
/******************************
** ↓ Mova suas formas aqui ↓ **
******************************/
```

Este trecho é executado a cada atualização da animação.

Movimento automático:

```java
bola.mover();
```

Movimento por teclado:

```java
if(right){
    jogador.setX(jogador.getX()+5);
}
```

Também é o local indicado para colisões e regras da aplicação.

### 4. Desenho das formas

```java
/*********************************
** ↓ Desenhe suas formas aqui ↓ **
*********************************/
```

```java
jogador.desenhar(g2d);
bola.desenhar(g2d);
```

## Fluxo da aplicação

```text
Declarar objetos
        ↓
Instanciar objetos
        ↓
Movimentar objetos
        ↓
Desenhar objetos
        ↓
Repaint
```

## Classe base

```java
public abstract class Forma{
    protected int x;
    protected int y;

    public abstract void desenhar(Graphics2D g2d);

    public void mover(){}
}
```

As classes `Retangulo`, `Circulo` e outras devem herdar de `Forma` e sobrescrever `desenhar()`.

## Exemplo de movimento automático

```java
public void mover(){
    x += velocidadeX;
    if(x<0 || x>760){
        velocidadeX *= -1;
    }
}
```

## Utilização do teclado

Flags existentes:

- `space`
- `up`
- `down`
- `left`
- `right`

Adicionar nova tecla:

```java
private boolean teclaA=false;
```

```java
if(e.getKeyCode()==KeyEvent.VK_A)
    teclaA=true;
```

```java
if(e.getKeyCode()==KeyEvent.VK_A)
    teclaA=false;
```

Utilização:

```java
if(teclaA){
    // ação
}
```

Qualquer constante `KeyEvent.VK_*` pode ser utilizada.

## Utilizando imagens

```java
BufferedImage imagem =
ImageIO.read(getClass().getResource("/images/personagem.png"));
```

```java
g2d.drawImage(imagem,x,y,largura,altura,null);
```

## Utilizando sons

```java
AudioInputStream audio =
AudioSystem.getAudioInputStream(
getClass().getResource("/audio/pulo.wav"));

Clip clip=AudioSystem.getClip();
clip.open(audio);
clip.start();
```

## Desafio Final

Utilize este projeto como base para desenvolver um jogo simples, como Pong, Snake, Breakout ou Space Shooter, reaproveitando toda a infraestrutura de janela, renderização, animação e entrada de teclado fornecida pela classe `ScreenSaver`.
