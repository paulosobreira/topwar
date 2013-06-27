package br.topwar;

import java.awt.Color;

public class ConstantesTopWar {
	/**
	 * Rede
	 */
	public static final String LISTA_AVATARES = "L_A";
	public static final String BALAS = "B";
	public static final String CARTUCHO = "C";
	public static final String PTS_VERMELHO = "P_V";
	public static final String PTS_AZUL = "P_A";
	public static final String TEMPO_JOGO_RESTANTE = "T_J_R";
	public static final String MOVER_PONTO = "M_P";
	public static final String ESPERE = "ESPERE";
	public static final String ALTERNA_FACA = "A_F";
	public static final int ARMA_FACA = 1;
	public static final int ARMA_ASSAULT = 2;
	public static final int ARMA_SHOTGUN = 3;
	public static final int ARMA_SNIPER = 4;
	public static final int ARMA_MACHINEGUN = 5;
	public static final int ARMA_ROCKET = 6;
	public static final int ARMA_SHIELD = 7;
	public static final int OBJ_ROCKET = 8;
	public static final String ATACAR = "A";
	public static final String KILL_CAM = "K_C";
	public static final String OBTER_PLCAR = "O_P";
	public static final String EVENTO_JOGO = "E_J";
	public static final String RADIO_JOGO = "R_J";
	public static final String MUDAR_CLASSE = "M_C";

	/**
	 * Gerais
	 */
	public static final String GRADE = "GRADE";
	public static final String BOT_GUIA = "BOT_GUIA";
	public static final String OBTER_DADOS_JOGO = "OBTER_DADOS_JOGO";
	public static final String CRIAR_JOGO = "CRIAR_JOGO";
	public static final String MOVER = "MOVER";
	public static final String ESQUERDA = "ESQUERDA";
	public static final String BAIXO = "BAIXO";
	public static final String DIREITA = "DIREITA";
	public static final String CIMA = "CIMA";
	public static final String ATUALIZAR_LISTA_AVS = "ATUALIZAR_LISTA_AVS";
	public static final String ENTRAR_JOGO = "ENTRAR_JOGO";
	public static final String SAIR_JOGO = "SAIR_JOGO";
	public static final int LARGURA_AVATAR = 35;
	public static final int ALTURA_AVATAR = 47;
	public static final int LARGURA_AREA_AVATAR = LARGURA_AVATAR / 2;
	public static final int ALTURA_AREA_AVATAR = ALTURA_AVATAR / 3;
	public static final String OK = "K";
	public static final String TIME_AZUL = "azul";
	public static final String TIME_VERMELHO = "vermelho";
	public final static Color lightWhite = new Color(255, 255, 255, 150);
	public final static Color lightBlu = new Color(180, 180, 255, 150);
	public final static Color lightRed = new Color(255, 180, 180, 150);
	public static final String ATUALIZA_ANGULO = "ATUALIZA_ANGULO";
	public static final String RECARREGAR = "R";
	public static final long ATRASO_REDE_PADRAO = 80;
	public static final long MEIO_ATRASO_REDE_PADRAO = ATRASO_REDE_PADRAO / 2;
	public static final long DUPLO_ATRASO_REDE_PADRAO = ATRASO_REDE_PADRAO * 2;

	public static final String ANGULO = "ANGULO";

	public static final int HEADSHOT = 0;
	public static final int LIMITE_VISAO = 450;
	public static final long ATRASO_REDE_PADRAO_BOTS = (long) (ATRASO_REDE_PADRAO * 1.5);

	/*
	 * ASSAULT
	 */
	public static final String ASSAULT = "assault";
	public static final int BALAS_ASSALT = 50;
	public static final int CARTUCHOS_ASSALT = 15;
	public static final int VIDA_COMPLETA_ASSALT = 100;
	public static final int VELOCIDADE_ASSAUT = 7;
	public static final int DESVIO_ASSAULT = 2;
	public static final int TEMPO_RECARGA_ASSAUT = 1000;
	public static final int ASSALT_MAX_RANGE = 1000;
	/*
	 * SHOTGUN
	 */
	public static final String SHOTGUN = "shotgun";
	public static final int VELOCIDADE_SHOTGUN = 8;
	public static final int BALAS_SHOTGUN = 6;
	public static final int CARTUCHOS_SHOTGUN = 3;
	public static final int VIDA_COMPLETA_SHOTGUN = 120;
	public static final long TEMPO_RECARGA_SHOTGUN = 900;
	/*
	 * SNIPER
	 */
	public static final String SNIPER = "sniper";
	public static final int BALAS_SNIPER = 1;
	public static final int CARTUCHOS_SNIPER = 16;
	public static final int VIDA_COMPLETA_SNIPER = 80;
	public static final int DESVIO_SNIPER = 0;
	public static final int TEMPO_RECARGA_SNIPER = 4000;
	public static final int VELOCIDADE_SNIPER = 6;
	public static final double LIMITE_VISAO_SNIPER = LIMITE_VISAO * 1.2;
	public static final int SNIPER_MAX_RANGE = 2000;
	/*
	 * MACHINEGUN
	 */
	public static final String MACHINEGUN = "machinegun";
	public static final int BALAS_MACHINEGUN = 300;
	public static final int CARTUCHOS_MACHINEGUN = 6;
	public static final int VIDA_COMPLETA_MACHINEGUN = 200;
	public static final int DESVIO_MACHINEGUN = 5;
	public static final int TEMPO_RECARGA_MACHINEGUN = 3000;
	public static final int VELOCIDADE_MACHINEGUN = 5;
	public static final int MACHINEGUN_MAX_RANGE = (int) (ASSALT_MAX_RANGE * 0.8);
	/*
	 * ROCKET
	 */
	public static final String ROCKET = "rocket";
	public static final int BALAS_ROCKET = 1;
	public static final int CARTUCHOS_ROCKET = 10;
	public static final int VIDA_COMPLETA_ROCKET = 80;
	public static final int VELOCIDADE_ROCKET = 4;
	public static final int TEMPO_RECARGA_ROCKET = 5000;
	public static final int DESVIO_ROCKET = 2;
	/*
	 * SHIELD
	 */
	public static final String SHIELD = "shield";
	public static final int VIDA_COMPLETA_SHIELD = 300;
	public static final int VELOCIDADE_SHIELD = 8;
	public static final long TEMPO_RECARGA_SHIELD = 10;

	public static final int VELOCIDADE_FACA = 8;
	public static final String RADIO_TIME = "RADIO_TIME";
	public static final String RADIO_TODOS = "RADIO_TODOS";
	public static final int NUMERO_JOGOS = 1;

}
