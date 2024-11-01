using UnityEngine;
using UnityEngine.SceneManagement;

public class LevelManager : MonoBehaviour
{
    public static LevelManager instance;
    private int lastLevelIndex; // 记录角色死亡前的关卡

    void Awake()
    {
        // 确保 LevelManager 在场景间保持唯一存在
        if (instance == null)
        {
            instance = this;
            DontDestroyOnLoad(gameObject);
        }
        else
        {
            Destroy(gameObject);
        }
    }

    // 记录当前关卡的索引
    public void SetLastLevelIndex()
    {
        lastLevelIndex = SceneManager.GetActiveScene().buildIndex;
    }

    // 切换到死亡场景
    public void LoadDeadScene()
    {
        SceneManager.LoadScene("Dead"); // "DeadScene" 是死亡场景的名字
    }

    // 重新加载死亡前的关卡
    public void ReloadLastLevel()
    {
        SceneManager.LoadScene(lastLevelIndex);
    }
}

