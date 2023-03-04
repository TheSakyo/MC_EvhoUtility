package fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.reflection;

import java.util.function.Function;

/**
 * Choisit une fonction à appliquer sur la base d'un fournisseur test, mémorise son choix et n'utilise que la fonction correspondante à l'avenir.
 * La branche à choisir est déterminée lors de la <em>première exécution de sa méthode </em>. {@link #apply(Object)} !</em>.
 * Cela signifie que, quel que soit le nombre de fois où la branche caractéristique est invoquée, elle ne reviendra jamais sur son choix.
 *
 * @param <T> Le type de l'entité à laquelle appliquer la fonction.
 * @param <R> Le type de retour de la fonction
 */
public class MemoizingFeatureBranch<T, R> {

    private final Function<T, Boolean> test;
    private final Function<T, R> trueBranch;
    private final Function<T, R> falseBranch;
    private volatile Function<T, R> chosen;

    /**
     * Crée une nouvelle {@link MemoizingFeatureBranch}, qui choisit entre deux fonctions données.
     *
     * @param test        Le fournisseur de test qui sera invoqué pour choisir une branche
     * @param trueBranch  La branche à choisir si le test est vrai
     * @param falseBranch La branche à choisir si le test est faux
     */
    public MemoizingFeatureBranch(Function<T, Boolean> test, Function<T, R> trueBranch, Function<T, R> falseBranch){

        this.test = test;
        this.trueBranch = trueBranch;
        this.falseBranch = falseBranch;
    }

    /**
     * Applique l'action stockée à la cible donnée et choisit la branche à utiliser lors du premier appel.
     *
     * @param target La cible à laquelle l'appliquer
     * @return Le résultat de l'application de la fonction à la cible donnée
     */
    public R apply(T target) {

        if(chosen == null) {

            synchronized(this) {

                if(chosen == null) chosen = test.apply(target) ? trueBranch : falseBranch;

            }
        }
        return chosen.apply(target);
    }

    /**
     * Crée une {@link MemoizingFeatureBranch} qui utilise le paramètre de réussite lorsque l'action se termine sans exception, sinon elle utilise le paramètre d'échec.
     * L'action est, selon la doc pour {@link MemoizingFeatureBranch} seulement appelée <em>une fois</em>.
     *
     * @param action  L'Action à invoquer
     * @param success La branche à prendre lorsque aucune exception ne se produit
     * @param failure La branche à prendre lorsqu'une exception se produit
     * @param <T>     le type de poignée
     * @param <R>     le type de résultat de l'action
     * @return un {@link MemoizingFeatureBranch} qui choisit la branche selon que l'action a déclenché une exception ou non.
     */
    public static <T, R> MemoizingFeatureBranch<T, R> onException(ExceptionalFunction<T, R> action, Function<T, R> success, Function<T, R> failure){
        return new MemoizingFeatureBranch<>(
                (t) -> {
                    try{
                        action.apply(t);
                        return true;
                    } catch(ExceptionalFunction.WrappedException e){
                        return false;
                    }
                },
                success, failure
        );
    }

    @FunctionalInterface
    public interface ExceptionalFunction<T, R> extends Function<T, R> {

        /**
         * Appelée par {@link #apply(Object)}, cette méthode est la cible de l'interface fonctionnelle et où vous pouvez écrire votre logique, qui pourrait lancer une exception.
         *
         * @param t L'Argument de la fonction
         * @return Le résultat de la fonction
         */
        R applyWithException(T t) throws Throwable;

        /**
         * {@inheritDoc}
         *
         * @param t {@inheritDoc}
         * @return {@inheritDoc}
         * @throws WrappedException Si un quelconque *Throwable* est déclenché
         */
        @Override
        default R apply(T t){
            try{
                return applyWithException(t);
            } catch(Throwable e){
                throw new WrappedException(e);
            }
        }

        class WrappedException extends RuntimeException {
            WrappedException(Throwable cause){
                super(cause);
            }
        }
    }
}
