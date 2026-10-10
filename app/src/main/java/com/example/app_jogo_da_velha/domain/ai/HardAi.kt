package com.example.app_jogo_da_velha.domain.ai

import com.example.app_jogo_da_velha.domain.GameLogic
import com.example.app_jogo_da_velha.model.Player

/**
 * Difícil: Minimax com poda alfa-beta. Joga de forma perfeita, então nunca perde.
 *
 * ## Minimax
 * O jogo da velha é pequeno o bastante para explorar a árvore inteira de jogadas
 * possíveis a partir do tabuleiro atual. Cada nó da árvore é um tabuleiro; os filhos são
 * os tabuleiros resultantes de cada jogada disponível. Nas folhas (vitória ou empate)
 * atribuímos uma pontuação:
 *
 * - vitória da IA      → `10 - depth`
 * - vitória do humano  → `depth - 10`
 * - empate             → `0`
 *
 * O `depth` (quantas jogadas foram simuladas até ali) faz a IA preferir vencer o mais
 * rápido possível (pontuação maior) e, se for perder, perder o mais tarde possível
 * (pontuação menos negativa), dando ao humano mais chances de errar.
 *
 * Subindo na árvore, os dois jogadores se alternam:
 * - na vez da IA (nó **maximizador**) escolhemos o filho de **maior** pontuação;
 * - na vez do humano (nó **minimizador**) supomos que ele joga o melhor possível para
 *   ele, ou seja, escolhemos o filho de **menor** pontuação.
 *
 * ## Poda alfa-beta
 * Durante a busca guardamos duas cotas:
 * - `alpha`: a melhor pontuação que a IA (maximizador) já tem garantida neste caminho;
 * - `beta`: a melhor pontuação que o humano (minimizador) já tem garantida.
 *
 * Se em algum nó `beta <= alpha`, o adversário nunca deixaria o jogo chegar até ali
 * (ele já tem uma alternativa melhor em outro ramo), então os filhos restantes podem ser
 * ignorados sem mudar o resultado. Isso reduz bastante o número de tabuleiros avaliados.
 */
class HardAi : AiPlayer {

    override fun chooseMove(board: List<Player?>, aiPlayer: Player): Int {
        // Otimização: com o tabuleiro vazio todas as jogadas empatam com jogo perfeito;
        // o centro é a escolha clássica e evita explorar a árvore inteira.
        if (board.all { it == null }) return CENTER

        // Avalia as jogadas na ordem centro → cantos → bordas. Como só uma pontuação
        // estritamente maior substitui a melhor até então, em caso de empate fica a
        // jogada que aparece primeiro nessa ordem.
        val moves = GameLogic.availableMoves(board).sortedBy { PREFERENCE.indexOf(it) }

        var bestMove = moves.first()
        var bestScore = Int.MIN_VALUE
        for (move in moves) {
            val next = GameLogic.applyMove(board, move, aiPlayer)
            // Depois da jogada da IA, é a vez do humano (minimizador)
            val score = minimax(next, depth = 1, isMaximizing = false,
                alpha = Int.MIN_VALUE, beta = Int.MAX_VALUE, aiPlayer = aiPlayer)
            if (score > bestScore) {
                bestScore = score
                bestMove = move
            }
        }
        return bestMove
    }

    /**
     * Retorna a pontuação de [board] supondo que, daqui em diante, os dois jogadores
     * jogam de forma perfeita.
     *
     * @param depth quantas jogadas já foram simuladas a partir do tabuleiro real
     * @param isMaximizing true se é a vez da IA, false se é a vez do humano
     * @param alpha melhor pontuação já garantida para a IA neste caminho
     * @param beta melhor pontuação já garantida para o humano neste caminho
     */
    fun minimax(
        board: List<Player?>,
        depth: Int,
        isMaximizing: Boolean,
        alpha: Int,
        beta: Int,
        aiPlayer: Player
    ): Int {
        // Folhas da árvore: partida encerrada
        GameLogic.checkWinner(board)?.let { (winner, _) ->
            return if (winner == aiPlayer) WIN_SCORE - depth else depth - WIN_SCORE
        }
        if (GameLogic.isDraw(board)) return 0

        var a = alpha
        var b = beta
        val player = if (isMaximizing) aiPlayer else aiPlayer.opponent

        return if (isMaximizing) {
            var best = Int.MIN_VALUE
            for (move in GameLogic.availableMoves(board)) {
                val score = minimax(GameLogic.applyMove(board, move, player),
                    depth + 1, isMaximizing = false, alpha = a, beta = b, aiPlayer = aiPlayer)
                best = maxOf(best, score)
                a = maxOf(a, best)
                if (b <= a) break // poda: o humano nunca deixaria chegar aqui
            }
            best
        } else {
            var best = Int.MAX_VALUE
            for (move in GameLogic.availableMoves(board)) {
                val score = minimax(GameLogic.applyMove(board, move, player),
                    depth + 1, isMaximizing = true, alpha = a, beta = b, aiPlayer = aiPlayer)
                best = minOf(best, score)
                b = minOf(b, best)
                if (b <= a) break // poda: a IA nunca deixaria chegar aqui
            }
            best
        }
    }

    private companion object {
        const val WIN_SCORE = 10
        const val CENTER = 4

        // Ordem de preferência no desempate: centro, cantos, bordas
        val PREFERENCE = listOf(4, 0, 2, 6, 8, 1, 3, 5, 7)
    }
}
