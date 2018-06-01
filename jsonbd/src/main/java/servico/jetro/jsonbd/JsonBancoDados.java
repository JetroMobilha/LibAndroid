package servico.jetro.jsonbd;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jetro Domigos on 13/05/2018.
 *
 */

public class JsonBancoDados {


    private SQLiteOpenHelper conet;
    private int limite = 15000;
    private String id = "id";
    private String token = "*";
    private String conf = "Conf";
    private String dados = "dados";
    public static String DADOS = "dados";
    public static String CONF = "Conf";
    private String tabela;
    private String culuna;
    private String wher;
    private String chaveWer;
    private SQLiteDatabase database;


    /**
     *  Construtor para o Objecto JsonBancoDados
     * @param conet objeto SQLiteOpenHelper para a concção com bd
     * @param tabela nome da tabela em banco de dados
     * @param culuna culuna onde esta o bd
     * @param wher coluna usada na intentificação da linha expecifica
     * @param chavWher dado usa para a comparação para encontrar a linha
     */
    public JsonBancoDados(SQLiteOpenHelper conet, String tabela, String culuna, String wher, String chavWher) {

        this.conet = conet;
        this.tabela = tabela;
        this.culuna = culuna;
        this.wher = wher;
        this.chaveWer = chavWher;

        try {
            JSONArray tabelas = getTabelasArray();

            if (tabelas != null && tabelas.length() > 0) {
              limite =   limite / tabelas.length();
            }
        }catch (Exception e){e.printStackTrace();}
    }


    /**
     *  Metodo usado para inserir uma linha em uma determinada tabela do banco de dados
     * @param nomeTabela nome da tabela em bd json
     * @param item para inserir na bd json
     * @return true se inserido false se falha
     */
    public boolean criarLinha(String nomeTabela, JSONObject item) {

        boolean retorno = false;
        try {

            //array de tabelas
            final JSONArray jsonArray = getTabelasArray();

            if (jsonArray != null && jsonArray.length() > 0) {

                // verificando se existe tabelas
                for (int i = 0; i < jsonArray.length(); i++) {

                    if (jsonArray.getJSONObject(i).has(nomeTabela)) {

                        JSONObject mTabela = jsonArray.getJSONObject(i);

                        if (!mTabela.getString(dados).isEmpty()) {

                            // incrementar id
                            mTabela.getJSONObject(conf).put(id
                                    ,mTabela.getJSONObject(conf).getInt(id)+1);

                            // atribuir id em elemento
                            item.put(id,mTabela.getJSONObject(conf).getInt(id));

                           String[] listItens = mTabela.getString(dados).split("#");

                            if (listItens.length > 0 ) { // tratando arra so com um elemento
                                JSONArray array =  new JSONArray(listItens[listItens.length-1]);

                                if (array.length() <limite) { // adicionando novo elememto
                                   array.put(item);

                                    listItens[listItens.length-1] = array.toString();

                                    StringBuilder liga = new StringBuilder();
                                    for (int e = 0; e < listItens.length ; e++) {
                                        if (e == 0) {
                                            liga = new StringBuilder(listItens[e]);
                                        } else {
                                            liga.append("#").append(listItens[e]);
                                        }
                                    }

                                    mTabela.put(dados, liga.toString());
                                    jsonArray.put(i,mTabela);

                                   retorno =  inserirTabelas(jsonArray);
                                } else {  // criando novo array
                                   JSONArray  novoArray = new JSONArray();
                                   novoArray.put(item);

                                    listItens[listItens.length] = novoArray.toString();

                                    StringBuilder liga = new StringBuilder();
                                    for (int e = 0; e < listItens.length ; e++) {
                                        if (e == 0) {
                                            liga = new StringBuilder(listItens[e]);
                                        } else {
                                            liga.append("#").append(listItens[e]);
                                        }
                                    }

                                    mTabela.put(dados, liga.toString());
                                    jsonArray.put(i,mTabela);

                                   retorno =  inserirTabelas(jsonArray);
                                }
                            }
                        } else {

                            item.put(id,mTabela.getJSONObject(conf).getInt(id));
                            JSONArray jsonArray1 = new JSONArray();

                            jsonArray1.put(item);

                           mTabela.put(dados,jsonArray1.toString());
                           jsonArray.put(i,mTabela);

                           retorno = inserirTabelas(jsonArray);
                        }

                        // Quebrando o laço depos de encontar a tabela deseja
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retorno;
    }


    /**
     *  Metodo usado para criar uma tabela em bd json
     * @param nomeTabelas para a nova tabela
     * @return true se inserido false se falha
     */
    public boolean criarTabela(String nomeTabelas) {

        try {
            JSONObject novoTabela = new JSONObject();

            // Array para a configuração
            JSONObject configuracao = new JSONObject();
            configuracao.put(this.id, 1);

            // tabela
            JSONObject tabelanova = new JSONObject();
            tabelanova.put(this.conf, configuracao);
            tabelanova.put(this.dados, "");


            novoTabela.put(nomeTabelas, tabelanova);

            String fonte = getfonte(this.tabela, this.culuna, this.wher, this.chaveWer);

            if (!fonte.isEmpty()) {
                // add tabela
                if (!fonte.isEmpty()) {
                    String[] dados = fonte.split(this.token);

                    JSONArray dados2 = new JSONArray();

                    for (String valor : dados) {
                        dados2.put(new JSONObject(valor));
                    }

                    dados2.put(novoTabela);

                    fonte = "";
                    for (int i = 0; i < dados2.length(); i++) {

                        if (fonte.isEmpty()) {
                            fonte = dados2.getJSONObject(i).toString();
                        } else {
                            fonte = fonte + this.token + dados2.getJSONObject(i).toString();
                        }
                    }

                    return inserir(fonte);
                    // criar no tabela
                } else {
                    return inserir(novoTabela.toString());
                }
            } else {
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    /*
     * Metodos para apagar dados
     */


    public boolean apagarBancaDeDados() {
        return inserir("" );
    }

    public boolean apagarTabela(String nomeTabela) {
        boolean retorno = false;
        try {
            JSONArray jsonArray = getTabelasArray();
            if (jsonArray != null && jsonArray.length() > 0) {

                // verificando se existe tabelas

                for (int i = 0; i < jsonArray.length(); i++) {

                    if (jsonArray.getJSONObject(i).has(nomeTabela)) {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            jsonArray.remove(i);
                        } else {
                            jsonArray.put(i, null);
                        }
                        retorno = inserirTabelas(jsonArray);
                        break;
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return retorno;
    }

    public boolean apagarLinha(String nomeTabela, String dadoExpecifico, String chave) {
        boolean retorno = false;

        try {
            JSONArray jsonArray = getTabelasArray();
            if (jsonArray != null && jsonArray.length() > 0) {

                // verificando se existe tabelas

                for (int i = 0; i < jsonArray.length(); i++) {

                    if (jsonArray.getJSONObject(i).has(nomeTabela)) {


                        JSONArray listaTabela = getObjetoArray(jsonArray.getJSONObject(i).getJSONObject(nomeTabela).getString(dados));

                        // procurando a existencia do dados
                        Integer id = null;
                        for (int s = 0; s < listaTabela.length(); s++) {

                            if (listaTabela.getJSONObject(s).has(chave)
                                    && listaTabela.getJSONObject(s).getString(chave).equals(dadoExpecifico)) {
                                id = s;
                                break;
                            }
                        }

                        // dado existente prosseco para apagar
                        if (id != null) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                listaTabela.remove(id);
                            } else {
                                listaTabela.put(id, null);
                            }

                            // refansendo a bd
                            JSONArray array_subs = new JSONArray();

                            for (int e = 0; e <= listaTabela.length(); e++) {

                                if (listaTabela.get(e) != null) {
                                    if (array_subs.length() == 0) {
                                        array_subs.put(new JSONArray());
                                        array_subs.getJSONArray(0).put(listaTabela.getJSONObject(e));

                                    } else if (array_subs.length() != 0 && array_subs.getJSONArray(array_subs.length() - 1).length() < limite) {

                                        array_subs.getJSONArray(array_subs.length() - 1).put(listaTabela.getJSONObject(e));
                                    } else {
                                        array_subs.put(new JSONArray());
                                        array_subs.getJSONArray(array_subs.length() - 1).put(listaTabela.getJSONObject(e));
                                    }
                                }
                            }


                            // codificando para quardar os ligados
                            StringBuilder liga = new StringBuilder();
                            for (int e = 0; e < array_subs.length(); e++) {
                                if (e == 0) {
                                    liga.append(array_subs.getJSONArray(e).toString());
                                } else {
                                    liga.append("#").append(array_subs.getJSONArray(e).toString());
                                }
                            }

                            jsonArray.getJSONObject(i).getJSONObject(nomeTabela).put(dados, liga.toString());
                            retorno = inserirTabelas(jsonArray);
                        }

                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();

        }

        return retorno;
    }

    /*
     * Metodos para atualizar tados
     */


    public boolean atualizarLinha(String nomeTabela, String dadoExpecifico, String chave, JSONObject item) {

        boolean retorno = false;

        try {
            JSONArray jsonArray = getTabelasArray();
            if (jsonArray != null && jsonArray.length() > 0) {

                // verificando se existe tabelas

                for (int i = 0; i < jsonArray.length(); i++) {

                    if (jsonArray.getJSONObject(i).has(nomeTabela)) {


                        JSONArray listaTabela = getObjetoArray(jsonArray.getJSONObject(i).getJSONObject(nomeTabela).getString(dados));

                        // procurando a existencia do dados
                        Integer id = null;
                        for (int s = 0; s < listaTabela.length(); s++) {

                            if (listaTabela.getJSONObject(s).has(chave)
                                    && listaTabela.getJSONObject(s).getString(chave).equals(dadoExpecifico)) {
                                id = s;
                                break;
                            }
                        }

                        // dado existente prosseco para apagar
                        if (id != null) {
                            listaTabela.put(id, item);

                            // refansendo a bd
                            JSONArray array_subs = new JSONArray();

                            for (int e = 0; e <= listaTabela.length(); e++) {

                                if (listaTabela.get(e) != null) {
                                    if (array_subs.length() == 0) {
                                        array_subs.put(new JSONArray());
                                        array_subs.getJSONArray(0).put(listaTabela.getJSONObject(e));

                                    } else if (array_subs.length() != 0 && array_subs.getJSONArray(array_subs.length() - 1).length() < limite) {

                                        array_subs.getJSONArray(array_subs.length() - 1).put(listaTabela.getJSONObject(e));
                                    } else {
                                        array_subs.put(new JSONArray());
                                        array_subs.getJSONArray(array_subs.length() - 1).put(listaTabela.getJSONObject(e));
                                    }
                                }
                            }


                            // codificando para quardar os ligados
                            StringBuilder liga = new StringBuilder();
                            for (int e = 0; e < array_subs.length(); e++) {
                                if (e == 0) {
                                    liga.append(array_subs.getJSONArray(e).toString());
                                } else {
                                    liga.append("#").append(array_subs.getJSONArray(e).toString());
                                }
                            }

                            jsonArray.getJSONObject(i).getJSONObject(nomeTabela).put(dados, liga.toString());
                            retorno = inserirTabelas(jsonArray);
                        }

                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();

        }

        return retorno;
    }

    /*
     * Metodos para gravar dados
     */


    /**
     *  Metodo usado para inser os dados na bd
     * @param item
     * @return
     */
    private boolean inserir(String item) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(culuna, item);

        database = this.conet.getWritableDatabase();
        int enserir = wher != null ? this.conet.getWritableDatabase().update(tabela, null, wher + "=?", new String[]{chaveWer})
                : database.update(tabela, null, null, null);
        database.close();

        return getBoolean(enserir);
    }


    /**
     *  Metodo usado para inserir uma lista de tabelas na bd
     * @param arrayTabelas
     * @return
     */
    private boolean inserirTabelas(JSONArray arrayTabelas) {
        try {
            StringBuilder fonte = new StringBuilder();

            for (int i = 0; i < arrayTabelas.length(); i++) {
                if (arrayTabelas.getJSONObject(i) != null) {
                    if (fonte.length() == 0) {
                        fonte = new StringBuilder(arrayTabelas.getJSONObject(i).toString());
                    } else {
                        fonte.append(token).append(arrayTabelas.getJSONObject(i).toString());
                    }
                }
            }
            return inserir(fonte.toString());
            // criar no tabela
        } catch (Exception e) {
            return false;
        }
    }

    /*
     * Metodos para recuperar dados
     */


    public JSONArray getLinhas(String nomeTabela) {

        JSONArray array = null;

        try {

            JSONArray tabelas = getTabelasArray();

            if (tabelas != null && tabelas.length() > 0) {
                for (int i = 0; i < tabelas.length(); i++) {

                    if (tabelas.getJSONObject(i).has(nomeTabela)) {
                        // procurando a existencia do dados

                        array = getObjetoArray(tabelas.getJSONObject(i).getJSONObject(nomeTabela).getString(dados));
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return array;
    }


    public JSONArray getLinhasPosicao(String nomeTabela, int posicao) {
        JSONArray retorno = new JSONArray();
        JSONArray array = getLinhas(nomeTabela);

        try {

            if (array != null) {

                int tamanho = 10;
                int conte = 0;
                posicao = posicao + 1;

                if (posicao <= array.getJSONObject(array.length() - 1).getInt(id)) {
                    Integer id = null;
                    // pegando o id do array na posição inicial
                    for (int i = 0; i < array.length(); i++) {

                        if (array.getJSONObject(i).getInt(this.id) == posicao) {
                            id = i;
                            break;
                        } else if (posicao == 0) {
                            id = 0;
                            break;
                        }
                    }

                    if (id != null) {

                        for (int e = id; e < array.length(); e++) {

                            if (array.getJSONObject(e).getInt(this.id) >= posicao) {
                                conte++;

                                retorno.put(array.getJSONObject(e));
                                posicao = array.getJSONObject(e).getInt(this.id);
                            }

                            if (conte == tamanho) {
                                break;
                            }
                        }
                    }


                }
            }

        } catch (Exception e) { e.printStackTrace();}

        return retorno;
    }


    public JSONArray getLinhasPorCulona(String nomeTabela, String culonaElemento) {
        JSONArray retorno = new JSONArray();

        try {

            JSONArray  array =getLinhas(nomeTabela);
            if (array!= null && array.length()> 0) {

                for (int i = 0; i < array.length(); i++) {

                    JSONObject jsonObject = new JSONObject();

                    jsonObject.put(id,array.getJSONObject(i).getInt(id));
                    jsonObject.put(culonaElemento,array.getJSONObject(i).getInt(culonaElemento));

                   retorno.put(jsonObject);
                }

            }

        }catch (Exception e){e.printStackTrace();}

        return retorno;
    }


    public JSONArray getLinhasPorCulonaPosicao(String nomeTabela,String culonaElemento, int posicao) {
        JSONArray retorno = new JSONArray();
        try {

            JSONArray  array =getLinhasPosicao(nomeTabela,posicao);
            if (array!= null && array.length()> 0) {

                for (int i = 0; i < array.length(); i++) {

                    JSONObject jsonObject = new JSONObject();

                    jsonObject.put(id,array.getJSONObject(i).getInt(id));
                    jsonObject.put(culonaElemento,array.getJSONObject(i).getInt(culonaElemento));

                    retorno.put(jsonObject);
                }
            }

        }catch (Exception e){e.printStackTrace();}

        return  retorno;
    }


    public JSONObject getLinhaPorCulona(String nomeTabela,String culonaElemento, String dadoExpecifico) {

               JSONObject jsonObject = null;
        try {
            JSONArray array = getLinhasPorCulona(nomeTabela, culonaElemento);
            if (array!=null) {

                for (int i = 0; i < array.length(); i++) {

                    if (array.getJSONObject(i).getString(culonaElemento).equals(dadoExpecifico)) {

                       jsonObject = array.getJSONObject(i);
                    }
                }


            }
        }catch (Exception e){e.printStackTrace();}

        return jsonObject;
    }


    public JSONObject getLinha(String nomeTabela,String chave, String dadoExpecifico) {

        JSONObject jsonObject = null;


        try {
            JSONArray array = getLinhas(nomeTabela);
            if (array!=null) {

                for (int i = 0; i < array.length(); i++) {

                    if (array.getJSONObject(i).getString(chave).equals(dadoExpecifico)) {

                        jsonObject = array.getJSONObject(i);
                    }
                }


            }
        }catch (Exception e){e.printStackTrace();}

        return jsonObject;
    }


    public JSONObject getTabela(String nomeTabela) {

        JSONObject jsonObject = null;
        try {
            JSONArray jsonArray = getTabelasArray();
            if (jsonArray != null && jsonArray.length() > 0) {

                // verificando se existe tabelas

                for (int i = 0; i < jsonArray.length(); i++) {

                    if (jsonArray.getJSONObject(i).has(nomeTabela)) {
                        jsonObject =  jsonArray.getJSONObject(i);
                        break;
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
         return jsonObject;
    }


    public JSONObject infTab(String nomeTabela) {

        JSONObject jsonObject = new JSONObject();
        try {
            JSONArray jsonArray = getTabelasArray();
            if (jsonArray != null && jsonArray.length() > 0) {

                // verificando se existe tabelas

                for (int i = 0; i < jsonArray.length(); i++) {

                    if (jsonArray.getJSONObject(i).has(nomeTabela)) {

                        jsonObject.put(id,jsonArray.getJSONObject(i).getJSONObject(nomeTabela).getJSONObject(conf).getInt(id));
                        jsonObject.put(dados,getObjetoArray(jsonArray.getJSONObject(i).getJSONObject(nomeTabela).getString(dados)) );

                        break;
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /*
     * Metodos utilitario
     */

    private JSONArray getTabelasArray() {
        try {
            return getObjetoTabelasArray(getfonte(tabela, culuna, wher, chaveWer));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }



    private String getfonte(String tabela, String coluna, String whereCulum, String whereValor) {

        String retorno = "";
        database = conet.getReadableDatabase();
        Cursor cursor = whereValor != null ? database.query(
                tabela
                , new String[]{coluna}
                , " WHERE " + whereCulum + "=?"
                , new String[]{whereValor}
                , null, null, null) : database.query(
                tabela
                , new String[]{coluna}
                , null
                , null
                , null, null, null);

        try {
            while (cursor.moveToNext()) {
                retorno = cursor.getString(cursor.getColumnIndex(coluna));
            }
        } finally {
            cursor.close();
        }

        database.close();
        return retorno;
    }

    private JSONArray getObjetoArray(String obj) {
        JSONArray retorno = new JSONArray();

        try {
            if (!obj.isEmpty()) {
                String[] array = obj.split("#");
                for (String s : array) {

                    JSONArray jsonArray = new JSONArray(s);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        retorno.put(jsonArray.getJSONObject(i));
                    }
                }
            }
        } catch (Exception e) {
        }

        return retorno;
    }

    private JSONArray getObjetoTabelasArray(String obj) throws JSONException {

        JSONArray jsonArray = new JSONArray();
        try {

            if (!obj.isEmpty()) {
                String[] array = obj.split(token);
                for (String o : array) {

                    jsonArray.put(new JSONObject(o));
                }
            }

        } catch (Exception e) {
        }

        return jsonArray;
    }

    public boolean getBoolean(long valor) {
        return valor > 0;
    }
}

