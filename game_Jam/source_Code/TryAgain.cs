using System.Collections;
using System.Collections.Generic;
using Unity.VisualScripting;
using UnityEngine;
using UnityEngine.SceneManagement;

public class TryAgain: MonoBehaviour
{
    public void ReloadGame()
    {
        LevelManager.instance.ReloadLastLevel();
    }
}
