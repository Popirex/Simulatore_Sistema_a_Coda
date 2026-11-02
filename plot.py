import json
import math 
import sys
from pathlib import Path

import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
import numpy as np

def erlang_c(c, lam, mu):
    """ Probabilità che un cliente debba aspettare """

    a = lam/ mu
    rho = a / c
    somma= sum(a ** k / math.factorial(k) for k in range(c))
    coda = a ** c / (math.factorial(c) * (1 - rho))
    return coda / (somma + coda) 

def teoria(c, lam, mu):
    """Restituisce le metriche teoriche (Ls, Lq, Ws, Wq, probAttesa)"""
    pw = erlang_c(c, lam, mu)
    wq = pw / (c * mu - lam)
    return{
        "Wq": wq,
        "Lq": lam * wq,
        "Ws": wq +1 / mu,
        "Ls": lam * wq + lam / mu,
        "probAttesa": pw,
    }

def teoria_pk(c, lam, mu, max_state):
    """ P(k) teoriche per k = 0 ... max_state l'ultima casella è 'max_state o più'"""
    a = lam / mu
    rho = a / c
    somma = sum(a ** k / (math.factorial(k)) for k in range(c))
    p0 = 1.0 / (somma + a ** c / (math.factorial(c) * (1-rho)))
    pk = []
    for k in range(max_state + 1):
        if k <= c:
            pk.append(p0 * a ** k / math.factorial(k))
        else: 
            pk.append(p0 * a ** k / (math.factorial(c) * c ** (k-c)))
    pk[-1] += max(0.0, 1.0 - sum(pk))
    return np.array(pk)    

def carica(path):
    with open(path, encoding="utf-8") as f:
        dati = json.load(f)
    return dati["configurazione"], dati["risultatiScenari"]

def per_c(scenari):
    """Raggruppa gli scenari per numero di server, ordinati per lambda"""
    gruppi = {}
    for s in scenari:
        gruppi.setdefault(s["c"], []).append(s)
    for c in gruppi:
        gruppi[c].sort(key=lambda s: s["lambda"])
    return dict(sorted(gruppi.items()))

def grafico_metriche(gruppi, mu, outdir):
    """Grafico 1: metriche principali (Ls, Lq, Ws, Wq) in funzione di lambda"""
    metriche=[("Ls", "Ls: clienti nel sistema"),
              ("Lq", "Lq: clienti in coda"),
              ("Ws", "Ws: tempo nel sistema"), 
              ("Wq", "Wq: tempo in coda")]
    colori = plt.get_cmap("tab10")
    fig, assi = plt.subplots(2, 2, figsize=(12, 9))

    for ax, (nome, titolo) in zip(assi.flat, metriche):
        for i, (c, lista) in enumerate(gruppi.items()):
            lam = np.array([s["lambda"] for s in lista])
            sim = np.array([s[nome]["media"] for s in lista])
            err = np.array([s[nome]["intConfid95"] for s in lista])
            colore = colori(i)

            """Curva teorica solo dove il sistema è stabile"""
            x = np.linspace(lam.min(), min(lam.max(), 0.999 * c * mu), 200)
            y = [teoria(c, l, mu)[nome] for l in x]
            ax.plot(x, y, color=colore, alpha=0.6)
            ax.errorbar(lam, sim, yerr=err, fmt="o", color=colore, capsize=3, label=f"c = {c}")
        
        ax.set_title(titolo)
        ax.set_xlabel("λ (tasso di arrivo)")
        ax.set_yscale("symlog", linthresh=1e-3)
        ax.grid(alpha=0.3)

    assi[0,0].legend(title="punti = simulazione (IC 95%)\nlinee = teoria")
    fig.suptitle("Simulazione vs Teoria M/M/c")
    fig.tight_layout()
    fig.savefig(outdir / "metriche.png", dpi=150)
    plt.close(fig)

def grafico_prob_attesa(gruppi, mu, outdir):
    """Grafico 2: probabilità di attesa"""
    colori = plt.get_cmap("tab10")
    fig, ax = plt.subplots(figsize=(7, 5))
    for i, (c, lista) in enumerate(gruppi.items()):
        lam = np.array([s["lambda"] for s in lista])
        sim = np.array([s["probAttesa"]["media"] for s in lista])
        err= np.array([s["probAttesa"]["intConfid95"] for s in lista])
        x = np.linspace(lam.min(), min(lam.max(), 0.999 * c * mu), 200)
        ax.plot(x, [erlang_c(c, l, mu) for l in x], color=colori(i), alpha=0.6)
        ax.errorbar(lam, sim, yerr=err, fmt="o", color=colori(i), capsize=3, label=f"c = {c}")
    ax.set_yscale("symlog", linthresh=1e-4)
    ax.set_xlabel("λ (tasso di arrivo)")
    ax.set_ylabel("P(attesa > 0)")
    ax.set_title("Probabilità di attesa: simulazione vs Erlang C")
    ax.legend()
    ax.grid(alpha=0.3)
    fig.tight_layout()
    fig.savefig(outdir / "prob_attesa.png", dpi=150)
    plt.close(fig)

def grafico_pk(scenari, mu, max_state, outdir):
    """Grafico 3: distribuzione di probabilità di stato P(k)"""

    """Prendo gli scenari con il rho più alto per ogni c"""
    gruppi = per_c(scenari)
    scelti = []
    for c, lista in gruppi.items():
        scelti.append(max(lista, key=lambda s: s["rho"]))
    scelti = scelti[:4] #Massimo 4

    n = len(scelti)
    colonne = 2 if n > 1 else 1
    righe = math.ceil(n / colonne)
    fig, assi = plt.subplots(righe, colonne, figsize=(6 * colonne, 4 * righe), squeeze=False)

    for ax, s in zip(assi.flat, scelti):
        c, lam = s["c"], s["lambda"]
        sim = np.array(s["mediaPk"])
        err = np.array(s["intConf95Pk"])
        teo = teoria_pk(c, lam, mu, max_state)

        #mostro solo gli stati con probabilità non trascurabile
        significativi = np.where((sim > 1e-4) | (teo > 1e-4))[0]
        kmax = int(significativi.max()) + 1 if len(significativi) else 10
        kmax = min(kmax, max_state)
        k = np.arange(kmax + 1)

        ax.bar(k, sim[: kmax +1], yerr=err[: kmax + 1], capsize=2, alpha=0.7, label="simulazione (IC 95%)")
        ax.plot(k, teo[: kmax + 1], "ro-", markersize=4, label="teoria")
        ax.set_title(f"c = {c},  λ = {lam}, ρ = {s['rho']:.2f}")
        ax.set_xlabel=("k (clienti nel sistema)")
        ax.set_ylabel=("P(k)")
        ax.legend()
        ax.grid(alpha=0.3)

    for ax in list(assi.flat)[n:]:
        ax.axis("off")
    fig.suptitle("Distruzione degli stati P(k)")
    fig.tight_layout()
    fig.savefig(outdir / "distrubuzione_pk.png", dpi=150)
    plt.close(fig)

def stampa_validazione(scenari, mu):
    """Tabella di validazione in console"""
    print(f"{'c':>3} {'lambda':>7} {'rho':>6} | "
          f"{'Ws sim':>8} {'Ws teo':>8} | {'Wq sim':>8} {'Wq teo':>8} | "
          f"{'Pw sim':>7} {'Pw teo':>7}")
    for s in sorted(scenari, key=lambda s: (s["c"], s["lambda"])):
        t = teoria(s["c"], s["lambda"], mu)
        print(f"{s['c']:>3} {s['lambda']:>7.2f} {s['rho']:>6.3f} | "
              f"{s['Ws']['media']:>8.4f} {t['Ws']:>8.4f} | "
              f"{s['Wq']['media']:>8.4f} {t['Wq']:>8.4f} | "
              f"{s['probAttesa']['media']:>7.4f} {t['probAttesa']:>7.4f}")

def main():
    path = Path(sys.argv[1]) if len(sys.argv) > 1 else Path("results/results.json")
    outdir = Path(sys.argv[2]) if len(sys.argv) > 2 else path.parent / "grafici"
    outdir.mkdir(parents=True, exist_ok=True)

    config, scenari = carica(path)
    mu = config["mu"]
    max_state = config["maxState"]
    gruppi = per_c(scenari)

    stampa_validazione(scenari, mu)
    grafico_metriche(gruppi, mu, outdir)
    grafico_prob_attesa(gruppi, mu, outdir)
    grafico_pk(scenari, mu, max_state, outdir)
    print(f"\nGrafici salvati in {outdir.resolve()}")

if __name__ == "__main__":
    main()