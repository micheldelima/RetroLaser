# 🔫 RetroLaser-Michel

Projeto desenvolvido para a disciplina de **Computação Gráfica** do curso de **Engenharia de Software / Ciência da Computação** — UEPG.

O projeto consiste em um **jogo 2D estilo retrô** implementado em **Java com Java2D**, explorando conceitos fundamentais de computação gráfica como rasterização de primitivas, transformações geométricas, animação, tipografia vetorial e composição de cenas.

---

## 🎯 Objetivo

Aplicar, na prática, os conceitos estudados na disciplina de Computação Gráfica por meio do desenvolvimento de um jogo 2D, utilizando a API **Java2D** para renderização de gráficos diretamente na tela (modo imediato), sem uso de engines gráficas prontas.

---

## 🛠️ Tecnologias Utilizadas

- **Java** (JDK 22)
- **Java2D** (`java.awt`, `java.awt.geom`, `java.awt.font`, `java.awt.image`)
- **Swing** (para a janela e o loop principal)
- **VS Code** como ambiente de desenvolvimento

---

## 🧠 Conceitos de Computação Gráfica Aplicados

### 1. Rasterização de Primitivas — Algoritmo de Bresenham
Implementado manualmente em `Bresenham.java` para traçado de linhas em uma grade discreta de pixels, sem uso de `g2d.drawLine()`. Demonstra como a GPU/CPU decide quais pixels acender entre dois pontos.

### 2. Pipeline Gráfico 2D e Sistema de Coordenadas
Uso do sistema de coordenadas do Java2D, com origem no canto superior esquerdo, eixo Y crescendo para baixo, e conversão entre coordenadas de mundo e de tela.

### 3. Tipografia Vetorial (`TextFX.java`)
Manipulação tipográfica avançada com:
- `FontMetrics` para alinhamento dinâmico e centralização de strings
- `GlyphVector.getOutline()` para extrair o **contorno vetorial** dos glifos
- `GradientPaint` para preencher o contorno com **gradiente de cor**
- **Sombra projetada** (string duplicada com offset)
- **Transformações afins** (`AffineTransform`) para texto inclinado / itálico customizado

### 4. Transformações Geométricas 2D
Uso de `AffineTransform` para rotação, translação e escala de elementos gráficos (naves, inimigos, tiros), evidenciando a composição de matrizes no pipeline.

### 5. Animação em Tempo Real
Loop principal em `GamePanel.java` com `Timer` do Swing, controlando taxa de atualização (FPS) e atualização de estado (game loop: *update → render*).

### 6. Efeitos Visuais e Composição
- Textos flutuantes (`FloatingText.java`) com atenuação (alpha) ao longo do tempo
- Sobrescrita de `paintComponent(Graphics g)` + cast para `Graphics2D` para acesso às primitivas avançadas

### 7. Detecção de Colisão e Lógica de Jogo
Colisão entre retângulos (`Rectangle.intersects`), gerenciamento de entidades (`Enemy`, `Shot`, `LaserBot`) e estado do jogo.

---

## 📂 Estrutura do Projeto

```
RetroLaser-Michel/
├── src/
│   ├── RetroLaser.java           # Classe principal (main)
│   ├── GamePanel.java            # Painel do jogo / loop principal
│   ├── LaserBot.java             # Entidade jogador
│   ├── Enemy.java                # Entidade inimiga
│   ├── Shot.java                 # Projéteis / tiros
│   ├── FloatingText.java         # Textos flutuantes animados
│   ├── TextFX.java               # Utilitários tipográficos (Java2D)
│   ├── Bresenham.java            # Rasterização de linhas (Bresenham)
│   ├── Star.java                 # Estrelas do cenário
│   └── OperacaoRetroLaser.java    # Operação/modo especial do jogo
├── bin/                          # Classes compiladas (.class)
└── README.md
```

---

## ▶️ Como Executar

### Pré-requisitos
- JDK 17 ou superior (testado com JDK 22)

### Compilar e executar

```bash
# Compilar
javac -d bin src/*.java

# Executar
java -cp bin RetroLaser
```

Ou, se estiver usando VS Code, basta abrir `RetroLaser.java` e clicar em **Run**.

---

## 🎮 Controles

| Tecla | Ação |
|-------|------|
| `←` `→` | Mover a nave |
| `Espaço` | Atirar |
| `Esc` | Sair / Pausar |

*(Ajuste conforme os controles reais implementados no `GamePanel`.)*

---

## 📸 Capturas de Tela

> Adicione aqui prints do jogo em execução para ilustrar o README.

```
<img width="957" height="708" alt="image" src="https://github.com/user-attachments/assets/1c1025dd-9c8a-4aeb-943f-e5a412cfc408" />

```

---

## 👤 Autor

**Michel de Lima**
Disciplina: Computação Gráfica — UEPG
Ano: 2026

---

## 📚 Referências

- [Oracle — Java 2D API](https://docs.oracle.com/javase/tutorial/2d/)
- [Oracle — Graphics2D](https://docs.oracle.com/javase/8/docs/api/java/awt/Graphics2D.html)
- [Oracle — AffineTransform](https://docs.oracle.com/javase/8/docs/api/java/awt/geom/AffineTransform.html)
- FOLEY, J. D. et al. *Computer Graphics: Principles and Practice*.
- HEARN, D.; BAKER, M. P. *Computer Graphics with OpenGL*.

---

## 📄 Licença

Projeto acadêmico sem fins comerciais. Uso livre para fins educacionais.
